package adotapet.testes;

import adotapet.modelo.Administrador;
import adotapet.modelo.Visitante;
import adotapet.servico.ArquivoPets;
import adotapet.servico.GerenciadorPets;
import adotapet.tela.TelaAdmin;
import adotapet.tela.TelaInicial;
import adotapet.tela.TelaVisitante;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Ferramenta auxiliar: capturas reais do Swing com exibição virtual (xvfb-run). */
public class CapturaInterface {
    public static void main(String[] args) throws Exception {
        Path destino = Paths.get(args.length > 0 ? args[0] : "docs");
        Files.createDirectories(destino);
        final GerenciadorPets dados = new GerenciadorPets(new ArquivoPets(Paths.get("dados", "pets.txt")));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        adotapet.tela.TemaUI.aplicarAparencia();
        capturar(new TelaInicial(dados), destino.resolve("01-tela-inicial.png"));
        capturar(new TelaAdmin(dados, Administrador.autenticar("admin", "admin123".toCharArray())),
                destino.resolve("02-tela-administrador.png"));
        capturarComSelecao(new TelaAdmin(dados, Administrador.autenticar("admin", "admin123".toCharArray())),
                destino.resolve("05-detalhes-pet.png"));
        capturar(new TelaVisitante(dados, new Visitante()), destino.resolve("03-tela-visitante.png"));
        capturarFormulario(destino.resolve("04-formulario-cadastro.png"));
        System.out.println("Capturas da interface salvas em " + destino.toAbsolutePath());
    }

    private static void capturarComSelecao(Window janela, Path arquivo) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            janela.setVisible(true);
            javax.swing.JTable tabela = localizarTabela((java.awt.Container) janela);
            if (tabela == null || tabela.getRowCount() == 0) throw new IllegalStateException("Tabela não disponível");
            tabela.setRowSelectionInterval(0, 0);
        });
        Thread.sleep(350);
        Robot robo = new Robot();
        ImageIO.write(robo.createScreenCapture(new Rectangle(
                janela.getLocationOnScreen().x, janela.getLocationOnScreen().y,
                janela.getWidth(), janela.getHeight())), "png", arquivo.toFile());
        SwingUtilities.invokeAndWait(() -> janela.dispose());
    }

    private static javax.swing.JTable localizarTabela(java.awt.Container c) {
        for (java.awt.Component filho : c.getComponents()) {
            if (filho instanceof javax.swing.JTable) return (javax.swing.JTable) filho;
            if (filho instanceof java.awt.Container) {
                javax.swing.JTable tabela = localizarTabela((java.awt.Container) filho);
                if (tabela != null) return tabela;
            }
        }
        return null;
    }

    private static void capturarFormulario(Path arquivo) throws Exception {
        final javax.swing.JFrame dono = new javax.swing.JFrame("Cadastro de pet");
        SwingUtilities.invokeAndWait(() -> {
            dono.setSize(700, 700);
            dono.setLocationRelativeTo(null);
            dono.setVisible(true);
            SwingUtilities.invokeLater(() -> adotapet.tela.DialogoPet.exibir(dono, null));
        });
        Thread.sleep(700);
        Window janela = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof javax.swing.JDialog && w.isShowing()) janela = w;
        }
        if (janela == null) throw new IllegalStateException("Formulário não abriu");
        Robot robo = new Robot();
        ImageIO.write(robo.createScreenCapture(new Rectangle(
                janela.getLocationOnScreen().x, janela.getLocationOnScreen().y,
                janela.getWidth(), janela.getHeight())), "png", arquivo.toFile());
        final Window dialogo = janela;
        SwingUtilities.invokeAndWait(() -> { dialogo.dispose(); dono.dispose(); });
    }

    private static void capturar(Window janela, Path arquivo) throws Exception {
        SwingUtilities.invokeAndWait(() -> janela.setVisible(true));
        Thread.sleep(400);
        Robot robo = new Robot();
        BufferedImage imagem = robo.createScreenCapture(new Rectangle(
                janela.getLocationOnScreen().x, janela.getLocationOnScreen().y,
                janela.getWidth(), janela.getHeight()));
        ImageIO.write(imagem, "png", arquivo.toFile());
        SwingUtilities.invokeAndWait(() -> janela.dispose());
    }
}
