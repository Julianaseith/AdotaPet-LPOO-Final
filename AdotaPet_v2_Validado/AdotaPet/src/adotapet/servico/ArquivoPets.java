package adotapet.servico;

import adotapet.modelo.Animal;
import adotapet.modelo.Cachorro;
import adotapet.modelo.Gato;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Persistência TXT (UTF-8) com gravação via arquivo temporário e troca atômica.
 * Cabeçalho: #ADOTAPET|1|<proximoId>
 * Registros: CAO|id|nome|idade|raca|sexo|disponivel|porte|energia[|foto]
 *            GATO|id|nome|idade|raca|sexo|disponivel|pelagem[|foto]
 * A foto opcional é somente o nome do arquivo dentro de dados/fotos/.
 * Registros antigos sem foto continuam compatíveis.
 * Delimitadores, barras e quebras de linha nos textos são escapados.
 */
public class ArquivoPets {
    private static final String ASSINATURA = "#ADOTAPET";
    private final Path caminho;

    public ArquivoPets(Path caminho) {
        if (caminho == null) {
            throw new IllegalArgumentException("Informe o caminho do arquivo.");
        }
        this.caminho = caminho.toAbsolutePath().normalize();
    }

    public Path getCaminho() {
        return caminho;
    }
    public Path getPastaFotos() {
        return caminho.getParent().resolve("fotos");
    }

    public static final class DadosLidos {
        private final ArrayList<Animal> animais;
        private final int proximoId;

        private DadosLidos(ArrayList<Animal> animais, int proximoId) {
            this.animais = animais;
            this.proximoId = proximoId;
        }
        public ArrayList<Animal> getAnimais() {
            return new ArrayList<Animal>(animais);
        }
        public int getProximoId() {
            return proximoId;
        }
    }

    /** Lê o cabeçalho, valida IDs únicos e reconstrói Cachorro ou Gato. */
    public DadosLidos carregar() throws IOException {
        ArrayList<Animal> lista = new ArrayList<Animal>();
        if (!Files.exists(caminho)) {
            return new DadosLidos(lista, 1);
        }
        Set<Integer> ids = new HashSet<Integer>();
        int proximoId;
        int maiorId = 0;
        // Charset declarado evita perda de caracteres acentuados entre sistemas operacionais.
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(
                Files.newInputStream(caminho), StandardCharsets.UTF_8))) {
            String cabecalho = leitor.readLine();
            List<String> partes = separar(cabecalho);
            if (partes.size() != 3 || !ASSINATURA.equals(partes.get(0)) || !"1".equals(partes.get(1))) {
                throw new IOException("Cabeçalho inválido no arquivo de animais. Não foram feitas alterações.");
            }
            try {
                proximoId = Integer.parseInt(partes.get(2));
            } catch (NumberFormatException erro) {
                throw new IOException("Próximo ID inválido no cabeçalho.", erro);
            }
            if (proximoId < 1) {
                throw new IOException("Próximo ID inválido no cabeçalho.");
            }
            String linha;
            int numeroLinha = 1;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    Animal pet = analisarRegistro(separar(linha));
                    if (!ids.add(pet.getId())) {
                        throw new IllegalArgumentException("ID duplicado: " + pet.getId());
                    }
                    maiorId = Math.max(maiorId, pet.getId());
                    lista.add(pet);
                } catch (IllegalArgumentException erro) {
                    throw new IOException("Registro inválido na linha " + numeroLinha + ": " + erro.getMessage(), erro);
                }
            }
        }
        if (proximoId <= maiorId) {
            throw new IOException("O próximo ID no cabeçalho é incompatível com os cadastros.");
        }
        return new DadosLidos(lista, proximoId);
    }

    private Animal analisarRegistro(List<String> campos) {
        if (campos.size() < 8) {
            throw new IllegalArgumentException("Quantidade de campos incorreta.");
        }
        String tipo = campos.get(0);
        int id = Integer.parseInt(campos.get(1));
        if (id < 1) {
            throw new IllegalArgumentException("ID deve ser positivo.");
        }
        String nome = campos.get(2);
        int idade = Integer.parseInt(campos.get(3));
        String raca = campos.get(4);
        String sexo = campos.get(5);
        if (!"true".equals(campos.get(6)) && !"false".equals(campos.get(6))) {
            throw new IllegalArgumentException("Situação do animal inválida.");
        }
        boolean disponivel = Boolean.parseBoolean(campos.get(6));
        Animal animal;
        if ("CAO".equals(tipo) && (campos.size() == 9 || campos.size() == 10)) {
            animal = new Cachorro(id, nome, idade, raca, sexo, disponivel, campos.get(7), campos.get(8));
            if (campos.size() == 10) {
                animal.setFoto(campos.get(9));
            }
            return animal;
        }
        if ("GATO".equals(tipo) && (campos.size() == 8 || campos.size() == 9)) {
            animal = new Gato(id, nome, idade, raca, sexo, disponivel, campos.get(7));
            if (campos.size() == 9) {
                animal.setFoto(campos.get(8));
            }
            return animal;
        }
        throw new IllegalArgumentException("Espécie ou quantidade de campos inválida.");
    }

    /** Escreve em arquivo temporário antes de substituir o TXT de dados. */
    public void salvar(List<Animal> animais, int proximoId) throws IOException {
        if (proximoId < 1) {
            throw new IllegalArgumentException("Próximo ID inválido.");
        }
        Path pasta = caminho.getParent();
        if (pasta == null) {
            throw new IOException("Pasta de destino inválida.");
        }
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "adotapet-", ".tmp");
        boolean substituido = false;
        try {
            try (PrintWriter escritor = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(temporario), StandardCharsets.UTF_8)))) {
                escritor.println(ASSINATURA + "|1|" + proximoId);
                for (Animal animal : animais) {
                    escritor.println(formatarRegistro(animal));
                }
                escritor.flush();
                if (escritor.checkError()) {
                    throw new IOException("Falha ao escrever os cadastros no arquivo temporário.");
                }
            }
            // A origem só é substituída após terminar toda a escrita.
            try {
                Files.move(temporario, caminho, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException erro) {
                Files.move(temporario, caminho, StandardCopyOption.REPLACE_EXISTING);
            }
            substituido = true;
        } finally {
            if (!substituido) {
                Files.deleteIfExists(temporario);
            }
        }
    }

    private String formatarRegistro(Animal animal) {
        String comum = "|" + animal.getId() + "|" + escapar(animal.getNome()) + "|"
                + animal.getIdade() + "|" + escapar(animal.getRaca()) + "|"
                + escapar(animal.getSexo()) + "|" + animal.isDisponivel();
        String foto = animal.getFoto().isEmpty() ? "" : "|" + escapar(animal.getFoto());
        if (animal instanceof Cachorro) {
            Cachorro cao = (Cachorro) animal;
            return "CAO" + comum + "|" + escapar(cao.getPorte()) + "|" + escapar(cao.getEnergia()) + foto;
        }
        if (animal instanceof Gato) {
            return "GATO" + comum + "|" + escapar(((Gato) animal).getPelagem()) + foto;
        }
        throw new IllegalArgumentException("Espécie não suportada.");
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("|", "\\|")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    /** Separa os campos respeitando o escape de barras, quebras de linha e delimitadores. */
    private static List<String> separar(String linha) throws IOException {
        if (linha == null) {
            throw new IOException("Arquivo vazio ou incompleto.");
        }
        ArrayList<String> campos = new ArrayList<String>();
        StringBuilder atual = new StringBuilder();
        boolean escapado = false;
        for (char c : linha.toCharArray()) {
            if (escapado) {
                switch (c) {
                    case 'n': atual.append('\n'); break;
                    case 'r': atual.append('\r'); break;
                    case '|': atual.append('|'); break;
                    case '\\': atual.append('\\'); break;
                    default: throw new IOException("Sequência de escape inválida no TXT.");
                }
                escapado = false;
            } else if (c == '\\') {
                escapado = true;
            } else if (c == '|') {
                campos.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        if (escapado) {
            throw new IOException("Barra de escape incompleta no TXT.");
        }
        campos.add(atual.toString());
        return campos;
    }
}
