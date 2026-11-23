package adotapet.servico;

import adotapet.excecao.PetException;
import adotapet.modelo.Animal;
import adotapet.modelo.Usuario;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Centraliza as operações do ArrayList e as permissões de acesso.
 * Em cada alteração, persiste uma cópia antes de atualizar a memória.
 */
public class GerenciadorPets {
    private final ArquivoPets arquivo;
    private ArrayList<Animal> animais;
    private int proximoId;

    public GerenciadorPets(ArquivoPets arquivo) throws IOException {
        if (arquivo == null) {
            throw new IllegalArgumentException("Arquivo não informado.");
        }
        this.arquivo = arquivo;
        ArquivoPets.DadosLidos dados = arquivo.carregar();
        this.animais = dados.getAnimais();
        this.proximoId = dados.getProximoId();
    }

    public String getCaminhoArquivo() {
        return arquivo.getCaminho().toString();
    }
    public Path getPastaFotos() {
        return arquivo.getPastaFotos();
    }

    public synchronized int getProximoId() {
        return proximoId;
    }
    public synchronized int getTotal() {
        return animais.size();
    }
    public synchronized int contarDisponiveisPorEspecie(String especie) {
        int total = 0;
        for (Animal animal : animais) {
            if (animal.isDisponivel() && animal.getEspecie().equals(especie)) {
                total++;
            }
        }
        return total;
    }

    public synchronized int getTotalDisponiveis() {
        int total = 0;
        for (Animal animal : animais) {
            if (animal.isDisponivel()) {
                total++;
            }
        }
        return total;
    }

    private void exigirAdministrador(Usuario usuario) throws PetException {
        if (usuario == null || !usuario.podeGerenciar()) {
            throw new PetException("Acesso negado. Esta ação exige perfil de administrador.");
        }
    }

    private Animal internoPorId(int id) throws PetException {
        for (Animal animal : animais) {
            if (animal.getId() == id) {
                return animal;
            }
        }
        throw new PetException("Nenhum animal encontrado com ID " + id + ".");
    }

    public synchronized Animal buscarPorId(Usuario usuario, int id) throws PetException {
        if (usuario == null) {
            throw new PetException("Acesso não autorizado.");
        }
        Animal animal = internoPorId(id);
        if (!usuario.podeGerenciar() && !animal.isDisponivel()) {
            throw new PetException("Este animal não está disponível para consulta pública.");
        }
        return animal.copiaComId(id);
    }

    /** Filtra registros conforme a permissão; devolve cópias para proteger a lista interna. */
    public synchronized List<Animal> listar(Usuario usuario, String busca, String especie,
                                             String situacao) throws PetException {
        if (usuario == null) {
            throw new PetException("Acesso não autorizado.");
        }
        ArrayList<Animal> resultado = new ArrayList<Animal>();
        String texto = normalizar(busca == null ? "" : busca);
        for (Animal animal : animais) {
            if (!usuario.podeGerenciar() && !animal.isDisponivel()) {
                continue;
            }
            if (especie != null && !especie.isEmpty() && !especie.equals(animal.getEspecie())) {
                continue;
            }
            if ("Disponíveis".equals(situacao) && !animal.isDisponivel()) {
                continue;
            }
            if ("Adotados".equals(situacao) && animal.isDisponivel()) {
                continue;
            }
            if (!texto.isEmpty() && !normalizar(animal.getNome() + " " + animal.getRaca()
                    + " " + animal.getEspecie() + " " + animal.getId()).contains(texto)) {
                continue;
            }
            resultado.add(animal.copiaComId(animal.getId()));
        }
        Collections.sort(resultado, new Comparator<Animal>() {
            @Override
            public int compare(Animal a, Animal b) {
                return Integer.compare(a.getId(), b.getId());
            }
        });
        return resultado;
    }

    private static String normalizar(String entrada) {
        String simples = Normalizer.normalize(entrada, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return simples.toLowerCase(Locale.ROOT).trim();
    }

    public synchronized Animal cadastrar(Usuario usuario, Animal dados) throws PetException, IOException {
        return cadastrar(usuario, dados, null);
    }

    /**
     * Importa a foto opcional e grava a lista proposta antes de alterar a memória.
     * Se a gravação falhar, descarta a foto recém-importada.
     */
    public synchronized Animal cadastrar(Usuario usuario, Animal dados, Path novaFoto)
            throws PetException, IOException {
        exigirAdministrador(usuario);
        if (dados == null || dados.getId() != 0) {
            throw new PetException("Para cadastrar, forneça um animal novo, ainda sem ID.");
        }
        if (proximoId == Integer.MAX_VALUE) {
            throw new PetException("Limite de IDs atingido.");
        }
        Animal cadastrado = dados.copiaComId(proximoId);
        String criada = "";
        try {
            if (novaFoto != null) {
                criada = FotoPets.importar(novaFoto, arquivo.getPastaFotos(), proximoId);
                cadastrado.setFoto(criada);
            }
            ArrayList<Animal> proposta = new ArrayList<Animal>(animais);
            proposta.add(cadastrado);
            persistir(proposta, proximoId + 1);
            return cadastrado.copiaComId(cadastrado.getId());
        } catch (IOException | RuntimeException erro) {
            limparFotoTemporaria(criada);
            throw erro;
        }
    }

    public synchronized void editar(Usuario usuario, int id, Animal novosDados) throws PetException, IOException {
        editar(usuario, id, novosDados, null);
    }

    /** Substitui o registro pelo ID, mantendo os demais animais sem alterações. */
    public synchronized void editar(Usuario usuario, int id, Animal novosDados, Path novaFoto)
            throws PetException, IOException {
        exigirAdministrador(usuario);
        if (novosDados == null) {
            throw new PetException("Informe os dados do animal.");
        }
        Animal anterior = internoPorId(id);
        Animal alterado = novosDados.copiaComId(id);
        String criada = "";
        try {
            if (novaFoto != null) {
                criada = FotoPets.importar(novaFoto, arquivo.getPastaFotos(), id);
                alterado.setFoto(criada);
            }
            ArrayList<Animal> proposta = new ArrayList<Animal>();
            for (Animal animal : animais) {
                proposta.add(animal.getId() == id ? alterado : animal);
            }
            persistir(proposta, proximoId);
            if (!anterior.getFoto().equals(alterado.getFoto())) {
                limparFotoNaoReferenciada(anterior.getFoto());
            }
        } catch (IOException | RuntimeException erro) {
            limparFotoTemporaria(criada);
            throw erro;
        }
    }

    private void limparFotoTemporaria(String foto) {
        if (foto.isEmpty()) {
            return;
        }
        try {
            Files.deleteIfExists(arquivo.getPastaFotos().resolve(foto));
        } catch (IOException ignorada) {
            // Falhas ao remover uma foto antiga não comprometem o TXT já salvo.
        }
    }

    /** Só apaga uma foto antiga quando nenhum cadastro continua usando-a. */
    private void limparFotoNaoReferenciada(String foto) {
        if (foto.isEmpty()) {
            return;
        }
        for (Animal animal : animais) {
            if (foto.equals(animal.getFoto())) {
                return;
            }
        }
        limparFotoTemporaria(foto);
    }

    public synchronized void alterarSituacao(Usuario usuario, int id, boolean disponivel)
            throws PetException, IOException {
        exigirAdministrador(usuario);
        Animal alterado = internoPorId(id).copiaComId(id);
        alterado.setDisponivel(disponivel);
        ArrayList<Animal> proposta = new ArrayList<Animal>();
        for (Animal animal : animais) {
            proposta.add(animal.getId() == id ? alterado : animal);
        }
        persistir(proposta, proximoId);
    }

    public synchronized void excluir(Usuario usuario, int id) throws PetException, IOException {
        exigirAdministrador(usuario);
        Animal removido = internoPorId(id);
        ArrayList<Animal> proposta = new ArrayList<Animal>();
        for (Animal animal : animais) {
            if (animal.getId() != id) {
                proposta.add(animal);
            }
        }
        persistir(proposta, proximoId);
        limparFotoNaoReferenciada(removido.getFoto());
    }

    /** Primeiro grava no disco; somente depois confirma o novo estado em memória. */
    private void persistir(ArrayList<Animal> proposta, int novoProximoId) throws IOException {
        arquivo.salvar(proposta, novoProximoId);
        animais = proposta;
        proximoId = novoProximoId;
    }
}
