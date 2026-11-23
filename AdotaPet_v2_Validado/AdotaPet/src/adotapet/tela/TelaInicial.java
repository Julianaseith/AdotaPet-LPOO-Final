package adotapet.tela;

import adotapet.modelo.Administrador;
import adotapet.modelo.Visitante;
import adotapet.servico.GerenciadorPets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Tela inicial e escolha de perfil. Shell compatível com JFrame Form do NetBeans. */
public class TelaInicial extends javax.swing.JFrame {
    private final GerenciadorPets gerenciador;

    public TelaInicial(GerenciadorPets gerenciador) {
        this.gerenciador = gerenciador;
        initComponents();
        setTitle("AdotaPet | Bem-vindo");
        setMinimumSize(new Dimension(790, 530));
        setSize(950, 600);
        setLocationRelativeTo(null);
        criarConteudo();
    }

    /** Constrói os dois cartões de acesso; o contêiner base vem do JFrame Form. */
    private void criarConteudo() {
        conteudo.setLayout(new BorderLayout());
        conteudo.setBackground(TemaUI.FUNDO);
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(TemaUI.AMARELO_MEL);
        topo.setBorder(BorderFactory.createEmptyBorder(22, 35, 22, 35));
        topo.add(TemaUI.texto("AdotaPet  ♥", 28, true, TemaUI.MARROM_CACAU), BorderLayout.WEST);
        topo.add(TemaUI.texto("Amor que acolhe • Adoção que transforma", 13, true, TemaUI.MARROM_CACAU), BorderLayout.EAST);
        conteudo.add(topo, BorderLayout.NORTH);

        JPanel centro = TemaUI.painelTransparente(new BorderLayout(0, 24));
        centro.setBorder(BorderFactory.createEmptyBorder(35, 55, 34, 55));
        JPanel saudacao = TemaUI.painelTransparente(new GridLayout(2, 1, 0, 3));
        JLabel titulo = TemaUI.texto("Bem-vindo ao AdotaPet ♥", 27, true, TemaUI.MARROM_CACAU);
        titulo.setHorizontalAlignment(JLabel.CENTER);
        JLabel descricao = TemaUI.texto("Cada patinha merece carinho e um lar. Escolha seu tipo de acesso.",
                14, false, TemaUI.SECUNDARIO);
        descricao.setHorizontalAlignment(JLabel.CENTER);
        saudacao.add(titulo);
        saudacao.add(descricao);
        centro.add(saudacao, BorderLayout.NORTH);

        JPanel opcoes = TemaUI.painelTransparente(new GridLayout(1, 2, 23, 0));
        opcoes.add(cartaoAcesso("Acesso da ONG", "ADMINISTRADOR", "Gerencie os cadastros, status e informações dos pets.",
                "Entrar como administrador", true));
        opcoes.add(cartaoAcesso("Encontre um amigo", "VISITANTE", "Consulte e encontre cães e gatos disponíveis para adoção.",
                "Explorar pets", false));
        centro.add(opcoes, BorderLayout.CENTER);
        conteudo.add(centro, BorderLayout.CENTER);

        JPanel rodape = TemaUI.painelTransparente(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        rodape.add(TemaUI.texto("Projeto acadêmico de LPOO  •  Java Swing  •  Armazenamento local",
                11, false, TemaUI.SECUNDARIO));
        conteudo.add(rodape, BorderLayout.SOUTH);
    }

    private JPanel cartaoAcesso(String titulo, String destaque, String descricao,
                                String botaoTexto, boolean administrar) {
        TemaUI.Cartao cartao = new TemaUI.Cartao(new BorderLayout(0, 13));
        cartao.setBorder(BorderFactory.createEmptyBorder(27, 24, 25, 24));
        JPanel textos = TemaUI.painelTransparente(new BorderLayout(0, 9));
        textos.add(TemaUI.texto(destaque, 11, true, TemaUI.TERRACOTA), BorderLayout.NORTH);
        JPanel interno = TemaUI.painelTransparente(new BorderLayout(0, 11));
        interno.add(TemaUI.texto(titulo, 24, true, TemaUI.MARROM_CACAU), BorderLayout.NORTH);
        javax.swing.JTextArea complemento = new javax.swing.JTextArea(descricao);
        complemento.setOpaque(false);
        complemento.setFont(TemaUI.fonte(Font.PLAIN, 14));
        complemento.setForeground(TemaUI.SECUNDARIO);
        complemento.setWrapStyleWord(true);
        complemento.setLineWrap(true);
        complemento.setEditable(false);
        complemento.setFocusable(false);
        interno.add(complemento, BorderLayout.CENTER);
        textos.add(interno, BorderLayout.CENTER);
        cartao.add(textos, BorderLayout.CENTER);
        JButton botao = TemaUI.botao(botaoTexto, administrar);
        botao.addActionListener(e -> {
            if (administrar) {
                acessarAdministrador();
            } else {
                acessarVisitante();
            }
        });
        cartao.add(botao, BorderLayout.SOUTH);
        return cartao;
    }

    /** Solicita credenciais didáticas antes de abrir o painel administrativo. */
    private void acessarAdministrador() {
        JTextField usuario = TemaUI.campo();
        JPasswordField senha = new JPasswordField();
        senha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUI.BORDA), BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        JPanel formulario = new JPanel(new GridLayout(4, 1, 0, 6));
        formulario.add(TemaUI.texto("Usuário", 12, true, TemaUI.TEXTO));
        formulario.add(usuario);
        formulario.add(TemaUI.texto("Senha", 12, true, TemaUI.TEXTO));
        formulario.add(senha);
        int escolha = JOptionPane.showConfirmDialog(this, formulario, "Acesso de administrador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (escolha != JOptionPane.OK_OPTION) return;
        char[] caracteres = senha.getPassword();
        Administrador administrador = Administrador.autenticar(usuario.getText(), caracteres);
        Arrays.fill(caracteres, '\0');
        if (administrador == null) {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.",
                    "Login não autorizado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TelaAdmin tela = new TelaAdmin(gerenciador, administrador);
        abrir(tela);
    }

    private void acessarVisitante() {
        TelaVisitante tela = new TelaVisitante(gerenciador, new Visitante());
        abrir(tela);
    }

    private void abrir(javax.swing.JFrame janela) {
        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                TelaInicial.this.setVisible(true);
            }
        });
        setVisible(false);
        janela.setVisible(true);
    }

    /**
     * Código inicial da estrutura do Form. O conteúdo visual é montado no método
     * criarConteudo(), permitindo compartilhar o tema entre os três JFrame Forms.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        conteudo = new javax.swing.JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        conteudo.setLayout(new java.awt.BorderLayout());
        getContentPane().add(conteudo, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel conteudo;
    // End of variables declaration//GEN-END:variables
}
