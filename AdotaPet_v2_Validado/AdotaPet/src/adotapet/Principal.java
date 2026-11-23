package adotapet;

import adotapet.servico.ArquivoPets;
import adotapet.servico.GerenciadorPets;
import adotapet.tela.TelaInicial;
import adotapet.tela.TemaUI;
import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Classe principal: inicia a aplicação no Event Dispatch Thread do Swing. */
public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignorada) {
                    // Usa a aparência padrão do Swing quando o tema do SO não está disponível.
                }
                TemaUI.aplicarAparencia();
                try {
                    GerenciadorPets gerenciador = new GerenciadorPets(
                            new ArquivoPets(Paths.get("dados", "pets.txt")));
                    TelaInicial tela = new TelaInicial(gerenciador);
                    tela.setVisible(true);
                } catch (IOException erro) {
                    JOptionPane.showMessageDialog(null,
                            "Não foi possível carregar o arquivo de animais.\n\n" + erro.getMessage()
                            + "\n\nVerifique dados/pets.txt antes de continuar. Nenhum dado foi apagado.",
                            "Erro ao iniciar o AdotaPet", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
