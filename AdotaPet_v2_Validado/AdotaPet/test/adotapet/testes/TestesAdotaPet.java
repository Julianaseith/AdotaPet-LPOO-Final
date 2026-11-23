package adotapet.testes;

import adotapet.excecao.PetException;
import adotapet.modelo.Administrador;
import adotapet.modelo.Animal;
import adotapet.modelo.Cachorro;
import adotapet.modelo.Gato;
import adotapet.modelo.Visitante;
import adotapet.servico.ArquivoPets;
import adotapet.servico.GerenciadorPets;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Testes sem dependências, executados com "ant test". */
public class TestesAdotaPet {
    private static int total = 0;
    private static int passou = 0;
    private static final Administrador ADMIN = Administrador.autenticar("admin", "admin123".toCharArray());
    private static final Visitante VISITANTE = new Visitante();

    private interface Execucao { void executar() throws Exception; }

    private static void verificar(boolean condicao, String descricao) {
        if (!condicao) throw new AssertionError(descricao);
    }
    private static void teste(String nome, Execucao execucao) throws Exception {
        total++;
        try {
            execucao.executar();
            passou++;
            System.out.println("[OK] " + nome);
        } catch (Exception | AssertionError e) {
            System.err.println("[FALHOU] " + nome + " -> " + e);
            throw e;
        }
    }
    private static <T extends Throwable> void esperar(Class<T> tipo, Execucao execucao) throws Exception {
        try { execucao.executar(); }
        catch (Throwable erro) {
            if (tipo.isInstance(erro)) return;
            throw new AssertionError("Esperado " + tipo.getSimpleName() + ", recebido " + erro, erro);
        }
        throw new AssertionError("Era esperado erro do tipo " + tipo.getSimpleName());
    }
    private static GerenciadorPets novo(Path pasta) throws IOException {
        return new GerenciadorPets(new ArquivoPets(pasta.resolve("pets.txt")));
    }
    private static Cachorro cao(String nome) {
        return new Cachorro(0, nome, 3, "SRD", "Macho", true, "Médio", "Alta");
    }
    private static Gato gato(String nome) {
        return new Gato(0, nome, 1, "Siamês", "Fêmea", true, "Curta");
    }

    public static void main(String[] args) throws Exception {
        final Path raiz = Files.createTempDirectory("adotapet-testes-");
        try {
            teste("login didático e polimorfismo de permissões", () -> {
                verificar(ADMIN != null && ADMIN.podeGerenciar(), "Administrador precisa gerenciar");
                verificar(!VISITANTE.podeGerenciar(), "Visitante não pode gerenciar");
                verificar(!ADMIN.getPermissoes().equals(VISITANTE.getPermissoes()), "Permissões devem diferir");
                verificar(Administrador.autenticar("admin", "errada".toCharArray()) == null,
                        "Senha incorreta não deve autenticar");
            });

            teste("classes abstratas, herança e cuidados polimórficos", () -> {
                List<Animal> lista = new ArrayList<Animal>();
                lista.add(cao("Bolinha"));
                lista.add(gato("Mimi"));
                verificar(!lista.get(0).getCuidadosEspeciais().equals(lista.get(1).getCuidadosEspeciais()),
                        "Cada espécie deve implementar cuidados próprios");
                verificar("Cachorro".equals(lista.get(0).getEspecie()), "Tipo cachorro");
                verificar("Gato".equals(lista.get(1).getEspecie()), "Tipo gato");
            });

            teste("cadastro com IDs automáticos únicos", () -> {
                GerenciadorPets g = novo(raiz.resolve("cadastro"));
                Animal a = g.cadastrar(ADMIN, cao("Thor"));
                Animal b = g.cadastrar(ADMIN, gato("Luna"));
                verificar(a.getId() == 1 && b.getId() == 2, "IDs esperados 1 e 2");
                verificar(g.getTotal() == 2 && g.getTotalDisponiveis() == 2, "Totais após cadastro");
            });

            teste("visitante vê apenas animais disponíveis", () -> {
                GerenciadorPets g = novo(raiz.resolve("visitante"));
                Animal a = g.cadastrar(ADMIN, cao("Bob"));
                g.cadastrar(ADMIN, gato("Lia"));
                g.alterarSituacao(ADMIN, a.getId(), false);
                verificar(g.listar(VISITANTE, "", "", "").size() == 1, "Disponíveis ao visitante");
                verificar(g.listar(ADMIN, "", "", "").size() == 2, "Todos ao administrador");
                esperar(PetException.class, () -> g.buscarPorId(VISITANTE, a.getId()));
            });

            teste("consulta por nome, raça, espécie, ID e sem acentos", () -> {
                GerenciadorPets g = novo(raiz.resolve("busca"));
                g.cadastrar(ADMIN, new Gato(0, "Coração", 2, "Siamês", "Fêmea", true, "Curta"));
                g.cadastrar(ADMIN, cao("Fido"));
                verificar(g.listar(VISITANTE, "coracao", "", "").size() == 1, "Busca sem acentos");
                verificar(g.listar(VISITANTE, "siames", "Gato", "").size() == 1, "Filtro raça/espécie");
                verificar(g.listar(VISITANTE, "2", "", "").size() == 1, "Busca por ID");
            });

            teste("edição de dados e mudança de espécie", () -> {
                GerenciadorPets g = novo(raiz.resolve("edicao"));
                Animal antigo = g.cadastrar(ADMIN, cao("Nico"));
                g.editar(ADMIN, antigo.getId(), gato("Nico 2"));
                Animal novo = g.buscarPorId(ADMIN, antigo.getId());
                verificar(novo instanceof Gato, "Espécie mudou");
                verificar("Nico 2".equals(novo.getNome()), "Nome atualizado");
            });

            teste("exclusão e próximo ID mesmo após reiniciar", () -> {
                Path pasta = raiz.resolve("exclusao");
                GerenciadorPets g = novo(pasta);
                g.cadastrar(ADMIN, cao("Um"));
                Animal segundo = g.cadastrar(ADMIN, gato("Dois"));
                g.excluir(ADMIN, segundo.getId());
                GerenciadorPets reiniciado = novo(pasta);
                verificar(reiniciado.getTotal() == 1, "Exclusão persistida");
                verificar(reiniciado.cadastrar(ADMIN, cao("Tres")).getId() == 3,
                        "ID excluído não pode ser reutilizado");
            });

            teste("gravação e leitura UTF-8 com caracteres especiais", () -> {
                Path pasta = raiz.resolve("acentos");
                GerenciadorPets g = novo(pasta);
                g.cadastrar(ADMIN, new Gato(0, "A|B\\C", 1, "Raça\nNova", "Fêmea", true, "Longa"));
                GerenciadorPets reaberto = novo(pasta);
                Animal pet = reaberto.buscarPorId(ADMIN, 1);
                verificar("A|B\\C".equals(pet.getNome()), "Escape de delimitador/barra");
                verificar("Raça\nNova".equals(pet.getRaca()), "Quebra de linha preservada");
                verificar(pet.getCuidadosEspeciais().contains("escovação"), "Acentos UTF-8");
            });

            teste("visitante não pode cadastrar, editar, excluir nem mudar status", () -> {
                GerenciadorPets g = novo(raiz.resolve("bloqueio"));
                Animal animal = g.cadastrar(ADMIN, cao("Protegido"));
                esperar(PetException.class, () -> g.cadastrar(VISITANTE, gato("Fraude")));
                esperar(PetException.class, () -> g.editar(VISITANTE, animal.getId(), gato("Fraude")));
                esperar(PetException.class, () -> g.excluir(VISITANTE, animal.getId()));
                esperar(PetException.class, () -> g.alterarSituacao(VISITANTE, animal.getId(), false));
                verificar(g.getTotal() == 1 && g.getTotalDisponiveis() == 1, "Permissões não alteram dados");
            });

            teste("cópias de consulta não alteram a lista interna", () -> {
                GerenciadorPets g = novo(raiz.resolve("copias"));
                g.cadastrar(ADMIN, cao("Original"));
                Animal copia = g.listar(VISITANTE, "", "", "").get(0);
                copia.setNome("Alterado sem permissão");
                copia.setDisponivel(false);
                verificar("Original".equals(g.buscarPorId(ADMIN, 1).getNome()), "Lista isolada");
                verificar(g.getTotalDisponiveis() == 1, "Status preservado");
            });

            teste("validação de nome, idade, sexo e ID", () -> {
                esperar(IllegalArgumentException.class, () -> cao("  "));
                esperar(IllegalArgumentException.class, () -> new Gato(0, "Mimi", -1, "SRD", "Fêmea", true, "Curta"));
                esperar(IllegalArgumentException.class, () -> new Gato(0, "Mimi", 101, "SRD", "Fêmea", true, "Curta"));
                esperar(IllegalArgumentException.class, () -> new Gato(0, "Mimi", 1, "SRD", "Outro", true, "Curta"));
                esperar(IllegalArgumentException.class, () -> new Gato(-1, "Mimi", 1, "SRD", "Fêmea", true, "Curta"));
            });

            teste("TXT inválido não é aceito nem sobrescrito", () -> {
                Path pasta = raiz.resolve("corrompido");
                Files.createDirectories(pasta);
                Path destino = pasta.resolve("pets.txt");
                String invalido = "#ADOTAPET|1|2\nGATO|1|Mimi|1|SRD|Fêmea|true|Curta\nGATO|1|Outra|2|SRD|Fêmea|true|Curta\n";
                Files.write(destino, invalido.getBytes(StandardCharsets.UTF_8));
                esperar(IOException.class, () -> novo(pasta));
                verificar(invalido.equals(new String(Files.readAllBytes(destino), StandardCharsets.UTF_8)),
                        "Arquivo deve permanecer intacto");
            });

            teste("erro de gravação mantém a memória anterior intacta", () -> {
                Path pasta = raiz.resolve("sem-escrita");
                Files.createDirectories(pasta);
                // O caminho pai a seguir é um arquivo comum, portanto não pode virar pasta.
                Files.write(pasta.resolve("bloqueio"), "X".getBytes(StandardCharsets.UTF_8));
                GerenciadorPets g = novo(pasta.resolve("bloqueio"));
                esperar(IOException.class, () -> g.cadastrar(ADMIN, cao("Falharia")));
                verificar(g.getTotal() == 0 && g.getProximoId() == 1, "Sem mutação após falha");
            });

            teste("TXT com cabeçalho ausente é rejeitado", () -> {
                Path pasta = raiz.resolve("sem-cabecalho");
                Files.createDirectories(pasta);
                Files.write(pasta.resolve("pets.txt"), "GATO|1|Lia|1|SRD|Fêmea|true|Curta\n".getBytes(StandardCharsets.UTF_8));
                esperar(IOException.class, () -> novo(pasta));
            });

            teste("fotos: cadastrar, salvar TXT e reencontrar depois de reiniciar", () -> {
                Path pasta = raiz.resolve("fotos-persistencia");
                Files.createDirectories(pasta);
                Path origem = pasta.resolve("foto-original.jpg");
                java.awt.image.BufferedImage imagem = new java.awt.image.BufferedImage(180, 120,
                        java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2 = imagem.createGraphics();
                g2.setColor(java.awt.Color.ORANGE);
                g2.fillRect(0, 0, 180, 120);
                g2.dispose();
                javax.imageio.ImageIO.write(imagem, "jpg", origem.toFile());
                GerenciadorPets g = novo(pasta);
                Animal pet = g.cadastrar(ADMIN, cao("Fotografado"), origem);
                verificar(!pet.getFoto().isEmpty(), "Foto deve ter nome de arquivo");
                Path salvo = g.getPastaFotos().resolve(pet.getFoto());
                verificar(Files.isRegularFile(salvo), "Arquivo copiado para dados/fotos");
                verificar(!pet.getFoto().contains("/"), "O TXT não guarda caminho absoluto");
                Files.delete(origem); // original deixa de existir; a cópia continua
                GerenciadorPets reaberto = novo(pasta);
                Animal recuperado = reaberto.buscarPorId(VISITANTE, pet.getId());
                verificar(pet.getFoto().equals(recuperado.getFoto()), "Referência à foto restaurada");
                verificar(Files.isRegularFile(reaberto.getPastaFotos().resolve(recuperado.getFoto())),
                        "Imagem permanece após fechar e abrir");
                verificar(new String(Files.readAllBytes(pasta.resolve("pets.txt")), StandardCharsets.UTF_8)
                        .contains("|" + pet.getFoto()), "Foto registrada no TXT");
            });

            teste("fotos: arquivo antigo sem imagem continua compatível", () -> {
                Path pasta = raiz.resolve("legado-fotos");
                Files.createDirectories(pasta);
                Files.write(pasta.resolve("pets.txt"),
                        ("#ADOTAPET|1|3\n" +
                        "CAO|1|Bidu|2|SRD|Macho|true|Pequeno|Alta\n" +
                        "GATO|2|Mimi|3|SRD|Fêmea|true|Curta\n").getBytes(StandardCharsets.UTF_8));
                GerenciadorPets g = novo(pasta);
                verificar(g.getTotal() == 2, "Antigos registros carregados");
                verificar(g.buscarPorId(ADMIN, 1).getFoto().isEmpty(), "Cachorro sem foto");
                verificar(g.buscarPorId(ADMIN, 2).getFoto().isEmpty(), "Gato sem foto");
                g.cadastrar(ADMIN, cao("Novo"));
                verificar(novo(pasta).getTotal() == 3, "Atualização preserva antigos registros");
            });

            teste("fotos: editar e remover substitui imagem persistida", () -> {
                Path pasta = raiz.resolve("fotos-edicao");
                Files.createDirectories(pasta);
                Path origem = pasta.resolve("imagem.png");
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(90, 70,
                        java.awt.image.BufferedImage.TYPE_INT_RGB);
                javax.imageio.ImageIO.write(img, "png", origem.toFile());
                GerenciadorPets g = novo(pasta);
                Animal pet = g.cadastrar(ADMIN, gato("Nina"), origem);
                String primeiraFoto = pet.getFoto();
                Animal outrosDados = gato("Nina Cresceu");
                g.editar(ADMIN, pet.getId(), outrosDados, origem);
                String segundaFoto = g.buscarPorId(ADMIN, pet.getId()).getFoto();
                verificar(!primeiraFoto.equals(segundaFoto), "Imagem substituída por novo arquivo");
                verificar(!Files.exists(g.getPastaFotos().resolve(primeiraFoto)), "Foto antiga foi limpa");
                Animal semFoto = g.buscarPorId(ADMIN, pet.getId());
                semFoto.setFoto("");
                g.editar(ADMIN, pet.getId(), semFoto);
                verificar(novo(pasta).buscarPorId(ADMIN, pet.getId()).getFoto().isEmpty(),
                        "Remoção foi persistida no TXT");
                verificar(!Files.exists(g.getPastaFotos().resolve(segundaFoto)), "Arquivo removido");
            });

            teste("fotos: imagem inválida não altera o cadastro", () -> {
                Path pasta = raiz.resolve("foto-invalida");
                Files.createDirectories(pasta);
                Path invalida = pasta.resolve("invalida.png");
                Files.write(invalida, "não é uma imagem".getBytes(StandardCharsets.UTF_8));
                GerenciadorPets g = novo(pasta);
                esperar(IOException.class, () -> g.cadastrar(ADMIN, cao("Teste"), invalida));
                verificar(g.getTotal() == 0 && g.getProximoId() == 1, "Erro não cria cadastro");
                verificar(!Files.exists(pasta.resolve("pets.txt")), "TXT não foi modificado");
                esperar(IllegalArgumentException.class, () -> cao("Teste").setFoto("../fora.png"));
            });

            teste("fotos: visitante consegue visualizar a miniatura", () -> {
                Path pasta = raiz.resolve("foto-visitante");
                Files.createDirectories(pasta);
                Path origem = pasta.resolve("foto.png");
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(150, 90,
                        java.awt.image.BufferedImage.TYPE_INT_RGB);
                javax.imageio.ImageIO.write(img, "png", origem.toFile());
                GerenciadorPets g = novo(pasta);
                g.cadastrar(ADMIN, cao("Bolinha"), origem);
                final java.util.concurrent.atomic.AtomicBoolean encontrou =
                        new java.util.concurrent.atomic.AtomicBoolean(false);
                javax.swing.SwingUtilities.invokeAndWait(() -> {
                    adotapet.tela.PainelCatalogo catalogo =
                            new adotapet.tela.PainelCatalogo(g, VISITANTE, () -> { });
                    javax.swing.JTable tabela = encontrarTabela(catalogo);
                    verificar(tabela != null && tabela.getRowCount() == 1, "Pet disponível ao visitante");
                    tabela.setRowSelectionInterval(0, 0);
                    encontrou.set(existeLabelComIcone(catalogo));
                });
                verificar(encontrou.get(), "Miniatura visível nos detalhes do visitante");
            });

            System.out.println("\nRESULTADO: " + passou + "/" + total + " testes passaram.");
        } finally {
            apagarRecursivo(raiz);
        }
    }

    private static javax.swing.JTable encontrarTabela(java.awt.Container container) {
        for (java.awt.Component componente : container.getComponents()) {
            if (componente instanceof javax.swing.JTable) return (javax.swing.JTable) componente;
            if (componente instanceof java.awt.Container) {
                javax.swing.JTable resultado = encontrarTabela((java.awt.Container) componente);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }

    private static boolean existeLabelComIcone(java.awt.Container container) {
        for (java.awt.Component componente : container.getComponents()) {
            if (componente instanceof javax.swing.JLabel &&
                    ((javax.swing.JLabel) componente).getIcon() != null) return true;
            if (componente instanceof java.awt.Container &&
                    existeLabelComIcone((java.awt.Container) componente)) return true;
        }
        return false;
    }

    private static void apagarRecursivo(Path local) throws IOException {
        if (!Files.exists(local)) return;
        if (Files.isDirectory(local)) {
            try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(local)) {
                for (Path p : stream) apagarRecursivo(p);
            }
        }
        Files.deleteIfExists(local);
    }
}
