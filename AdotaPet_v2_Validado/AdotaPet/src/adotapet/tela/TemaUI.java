package adotapet.tela;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Estilo visual compartilhado pelas janelas, sem bibliotecas externas. */
public final class TemaUI {
    // Paleta "Amarelo Mel": sol, carinho e acolhimento.
    // Constantes com nomes das cores efetivamente usadas nas telas.
    public static final Color FUNDO = new Color(255, 248, 237);       // Creme claro
    public static final Color MARROM_CACAU = new Color(82, 49, 31);    // Títulos e contraste
    public static final Color AMARELO_MEL = new Color(255, 196, 69);    // Ações principais
    public static final Color TERRACOTA = new Color(155, 77, 39);     // Destaques
    public static final Color TEXTO = new Color(72, 48, 31);         // Marrom intenso
    public static final Color SECUNDARIO = new Color(119, 96, 76);   // Areia escura
    public static final Color BORDA = new Color(233, 212, 182);      // Bege delicado
    public static final Color SUAVE = new Color(255, 237, 198);      // Manteiga
    public static final Color PERIGO = new Color(178, 66, 70);       // Vermelho queimado
    public static final Color CARTAO = new Color(255, 254, 250);     // Marfim
    public static final Color MEL_FORTE = new Color(242, 172, 37);   // Realce ao passar o mouse
    public static final Color MARROM_CLARO = new Color(119, 68, 43); // Gradiente para terracota

    private TemaUI() {
    }

    /** Harmoniza componentes nativos onde o sistema operacional permitir. */
    public static void aplicarAparencia() {
        javax.swing.UIManager.put("ComboBox.background", CARTAO);
        javax.swing.UIManager.put("ComboBox.foreground", TEXTO);
        javax.swing.UIManager.put("ComboBox.selectionBackground", SUAVE);
        javax.swing.UIManager.put("ComboBox.selectionForeground", TEXTO);
        javax.swing.UIManager.put("ComboBox.buttonBackground", SUAVE);
        javax.swing.UIManager.put("TextField.selectionBackground", SUAVE);
        javax.swing.UIManager.put("TextField.selectionForeground", TEXTO);
        javax.swing.UIManager.put("Table.selectionBackground", SUAVE);
        javax.swing.UIManager.put("Table.selectionForeground", TEXTO);
    }

    public static Font fonte(int estilo, int tamanho) {
        return new Font("SansSerif", estilo, tamanho);
    }

    public static JLabel texto(String valor, int tamanho, boolean destaque, Color cor) {
        JLabel label = new JLabel(valor);
        label.setFont(fonte(destaque ? Font.BOLD : Font.PLAIN, tamanho));
        label.setForeground(cor);
        return label;
    }

    public static void margem(JComponent componente, int cima, int esquerda, int baixo, int direita) {
        componente.setBorder(new EmptyBorder(cima, esquerda, baixo, direita));
    }

    public static JPanel painelTransparente(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JTextField campo() {
        JTextField campo = new JTextField();
        campo.setFont(fonte(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1, true), new EmptyBorder(9, 12, 9, 12)));
        campo.setPreferredSize(new Dimension(180, 39));
        return campo;
    }

    public static JTextField campoPesquisa(final String dica) {
        JTextField campo = new JTextField() {
            @Override
            protected void paintComponent(Graphics grafico) {
                super.paintComponent(grafico);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g = (Graphics2D) grafico.create();
                    g.setColor(SECUNDARIO);
                    g.setFont(fonte(Font.PLAIN, 13));
                    java.awt.FontMetrics fonte = g.getFontMetrics();
                    g.drawString(dica, 12, (getHeight() + fonte.getAscent() - fonte.getDescent()) / 2);
                    g.dispose();
                }
            }
        };
        campo.setFont(fonte(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1, true), new EmptyBorder(9, 12, 9, 12)));
        campo.setPreferredSize(new Dimension(180, 39));
        return campo;
    }

    /** Aplica cores e efeitos de interação uniformes aos botões. */
    public static JButton botao(String texto, boolean principal) {
        return new BotaoRedondo(texto, principal ? AMARELO_MEL : CARTAO,
                MARROM_CACAU, principal ? MEL_FORTE : SUAVE);
    }

    /** Painel com fundo arredondado, reutilizado nos cartões da interface. */
    public static class Cartao extends JPanel {
        private final Color cor;
        private final int raio;

        public Cartao(java.awt.LayoutManager layout) {
            this(layout, CARTAO, 18);
        }
        public Cartao(java.awt.LayoutManager layout, Color cor, int raio) {
            super(layout);
            this.cor = cor;
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D g = (Graphics2D) grafico.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(cor);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g.dispose();
            super.paintComponent(grafico);
        }
    }

    /** Componente visual: desenha bordas arredondadas e realce no mouse. */
    private static class BotaoRedondo extends JButton {
        private final Color normal;
        private final Color realce;
        private final Color corTexto;
        private boolean hover;

        BotaoRedondo(String texto, Color normal, Color corTexto, Color realce) {
            super(texto);
            this.normal = normal;
            this.realce = realce;
            this.corTexto = corTexto;
            setFont(fonte(Font.BOLD, 13));
            setForeground(corTexto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(11, 17, 11, 17));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(Math.max(128, getPreferredSize().width), 40));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true; repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D g = (Graphics2D) grafico.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(hover || getModel().isPressed() ? realce : normal);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
            if (normal.equals(CARTAO)) {
                g.setColor(BORDA);
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 13, 13);
            }
            g.dispose();
            super.paintComponent(grafico);
        }
    }
}
