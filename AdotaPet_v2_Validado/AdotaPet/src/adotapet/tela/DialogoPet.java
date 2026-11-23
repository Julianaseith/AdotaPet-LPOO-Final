package adotapet.tela;

import adotapet.modelo.Animal;
import adotapet.modelo.Cachorro;
import adotapet.modelo.Gato;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.nio.file.Path;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Formulário modal para criação ou edição de um cachorro ou gato. */
public class DialogoPet extends JDialog {
    private final JComboBox<String> especie = new JComboBox<String>(new String[]{"Cachorro", "Gato"});
    private final JTextField nome = TemaUI.campo();
    private final JTextField idade = TemaUI.campo();
    private final JTextField raca = TemaUI.campo();
    private final JComboBox<String> sexo = new JComboBox<String>(new String[]{"Macho", "Fêmea"});
    private final JComboBox<String> situacao = new JComboBox<String>(new String[]{"Disponível", "Adotado"});
    private final JComboBox<String> porte = new JComboBox<String>(new String[]{"Pequeno", "Médio", "Grande"});
    private final JComboBox<String> energia = new JComboBox<String>(new String[]{"Baixa", "Média", "Alta"});
    private final JComboBox<String> pelagem = new JComboBox<String>(new String[]{"Curta", "Média", "Longa", "Sem pelos"});
    private final JPanel especiesPanel = new JPanel(new CardLayout());
    private Animal resultado;
    private final Animal original;
    private final Path pastaFotos;
    private final JLabel previaFoto = new JLabel();
    private Path novaFoto;
    private String fotoSalva = "";

    public static final class Resultado {
        private final Animal animal;
        private final Path novaFoto;
        private Resultado(Animal animal, Path novaFoto) {
            this.animal = animal;
            this.novaFoto = novaFoto;
        }
        public Animal getAnimal() {
            return animal;
        }
        public Path getNovaFoto() {
            return novaFoto;
        }
    }

    private DialogoPet(Window dono, Animal original, Path pastaFotos) {
        super(dono, original == null ? "Cadastrar pet" : "Editar pet", ModalityType.APPLICATION_MODAL);
        this.original = original;
        this.pastaFotos = pastaFotos;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(815, 745);
        setMinimumSize(new Dimension(710, 650));
        setLocationRelativeTo(dono);
        montar();
        if (original != null) {
            preencher(original);
        }
        especie.addActionListener(e -> trocarCampos());
        trocarCampos();
    }

    /** Compatibilidade com chamadas anteriores sem escolha de pasta de fotos. */
    public static Animal exibir(Component componente, Animal original) {
        Resultado resultado = exibir(componente, original, java.nio.file.Paths.get("dados", "fotos"));
        return resultado == null ? null : resultado.getAnimal();
    }

    public static Resultado exibir(Component componente, Animal original, Path pastaFotos) {
        Window dono = SwingUtilities.getWindowAncestor(componente);
        DialogoPet dialogo = new DialogoPet(dono, original, pastaFotos);
        dialogo.setVisible(true);
        return dialogo.resultado == null ? null : new Resultado(dialogo.resultado, dialogo.novaFoto);
    }

    private static void formatarCombo(JComboBox<String> combo) {
        combo.setFont(TemaUI.fonte(Font.PLAIN, 14));
        combo.setBackground(java.awt.Color.WHITE);
        combo.setPreferredSize(new Dimension(230, 38));
    }

    private JPanel novoPainelCampos() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        return painel;
    }

    private void inserirCampo(JPanel painel, String titulo, Component campo, int linha) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = linha * 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(linha == 0 ? 2 : 9, 0, 3, 0);
        painel.add(TemaUI.texto(titulo, 12, true, TemaUI.TEXTO), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 2, 0);
        painel.add(campo, c);
    }

    /** Monta os campos comuns, os campos por espécie e a área de fotografia. */
    private void montar() {
        getContentPane().setBackground(TemaUI.FUNDO);
        getContentPane().setLayout(new BorderLayout());
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(TemaUI.AMARELO_MEL);
        TemaUI.margem(topo, 20, 26, 20, 26);
        JLabel titulo = TemaUI.texto(original == null ? "Novo animal" : "Editar cadastro", 23, true, TemaUI.MARROM_CACAU);
        topo.add(titulo, BorderLayout.CENTER);
        getContentPane().add(topo, BorderLayout.NORTH);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 27, 10, 27));
        formatarCombo(especie);
        formatarCombo(sexo);
        formatarCombo(situacao);
        formatarCombo(porte);
        formatarCombo(energia);
        formatarCombo(pelagem);

        JPanel principal = novoPainelCampos();
        inserirCampo(principal, "Espécie", especie, 0);
        inserirCampo(principal, "Nome do animal", nome, 1);
        inserirCampo(principal, "Idade em anos", idade, 2);
        inserirCampo(principal, "Raça", raca, 3);
        inserirCampo(principal, "Sexo", sexo, 4);
        inserirCampo(principal, "Situação para adoção", situacao, 5);

        JPanel cachorro = novoPainelCampos();
        inserirCampo(cachorro, "Porte do cachorro", porte, 0);
        inserirCampo(cachorro, "Nível de energia", energia, 1);
        JPanel gato = novoPainelCampos();
        inserirCampo(gato, "Pelagem do gato", pelagem, 0);
        especiesPanel.setOpaque(false);
        especiesPanel.add(cachorro, "Cachorro");
        especiesPanel.add(gato, "Gato");

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        painel.add(principal, c);
        c.gridy = 1;
        c.anchor = GridBagConstraints.NORTH;
        painel.add(especiesPanel, c);
        c.gridy = 2;
        c.weighty = 1;
        JPanel espacador = new JPanel();
        espacador.setOpaque(false);
        painel.add(espacador, c);
        JPanel corpo = new JPanel(new BorderLayout(8, 0));
        corpo.setOpaque(false);
        corpo.add(painel, BorderLayout.CENTER);
        corpo.add(montarPainelFoto(), BorderLayout.EAST);
        JScrollPane rolagem = new JScrollPane(corpo);
        rolagem.getViewport().setBackground(TemaUI.FUNDO);
        rolagem.setBackground(TemaUI.FUNDO);
        rolagem.setBorder(null);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        getContentPane().add(rolagem, BorderLayout.CENTER);
        atualizarPrevia();

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(5, 24, 20, 26));
        JButton cancelar = TemaUI.botao("Cancelar", false);
        JButton salvar = TemaUI.botao(original == null ? "Cadastrar pet" : "Salvar alterações", true);
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> validarSalvar());
        rodape.add(cancelar);
        rodape.add(salvar);
        getContentPane().add(rodape, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(salvar);
        getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fechar");
        getRootPane().getActionMap().put("fechar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /** Mantém a escolha de imagem opcional separada dos dados do animal. */
    private JPanel montarPainelFoto() {
        JPanel externo = new JPanel(new BorderLayout(0, 12));
        externo.setOpaque(false);
        externo.setPreferredSize(new Dimension(250, 340));
        externo.setBorder(BorderFactory.createEmptyBorder(27, 3, 0, 24));
        JPanel superior = new JPanel(new BorderLayout(0, 9));
        superior.setOpaque(false);
        superior.add(TemaUI.texto("FOTO DO ANIMAL (OPCIONAL)", 12, true, TemaUI.TEXTO), BorderLayout.NORTH);
        previaFoto.setPreferredSize(new Dimension(220, 180));
        previaFoto.setBorder(BorderFactory.createLineBorder(TemaUI.BORDA));
        superior.add(previaFoto, BorderLayout.CENTER);
        externo.add(superior, BorderLayout.NORTH);
        JPanel acoes = new JPanel(new GridBagLayout());
        acoes.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 8, 0);
        JButton escolher = TemaUI.botao("Selecionar foto", true);
        escolher.addActionListener(e -> escolherFoto());
        acoes.add(escolher, c);
        c.gridy = 1;
        JButton remover = TemaUI.botao("Remover foto", false);
        remover.addActionListener(e -> {
            novaFoto = null;
            fotoSalva = "";
            atualizarPrevia();
        });
        acoes.add(remover, c);
        c.gridy = 2;
        c.insets = new Insets(9, 0, 0, 0);
        JLabel orientacao = TemaUI.texto("JPG ou PNG • até 12 MB", 11, false, TemaUI.SECUNDARIO);
        acoes.add(orientacao, c);
        externo.add(acoes, BorderLayout.CENTER);
        return externo;
    }

    /** Valida tamanho e formato antes de mostrar a imagem escolhida. */
    private void escolherFoto() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecione uma foto do animal");
        seletor.setAcceptAllFileFilterUsed(false);
        seletor.setFileFilter(new FileNameExtensionFilter("Fotos JPG ou PNG", "jpg", "jpeg", "png"));
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path arquivo = seletor.getSelectedFile().toPath();
        try {
            if (java.nio.file.Files.size(arquivo) > 12L * 1024L * 1024L) {
                JOptionPane.showMessageDialog(this, "A foto deve ter no máximo 12 MB.",
                        "Foto muito grande", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (javax.imageio.ImageIO.read(arquivo.toFile()) == null) {
                JOptionPane.showMessageDialog(this, "O arquivo não contém uma foto válida.",
                        "Foto inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            novaFoto = arquivo;
            atualizarPrevia();
        } catch (java.io.IOException erro) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir essa foto: " + erro.getMessage(),
                    "Erro na foto", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Mostra uma foto nova, a foto já salva ou o aviso de imagem ausente. */
    private void atualizarPrevia() {
        Path caminho = novaFoto != null ? novaFoto :
                (fotoSalva.isEmpty() ? null : pastaFotos.resolve(fotoSalva));
        ImagemPet.mostrar(previaFoto, caminho, 218, 175);
    }

    /** Alterna entre campos de cachorro e de gato usando CardLayout. */
    private void trocarCampos() {
        ((CardLayout) especiesPanel.getLayout()).show(especiesPanel, (String) especie.getSelectedItem());
    }

    private void preencher(Animal animal) {
        fotoSalva = animal.getFoto();
        atualizarPrevia();
        especie.setSelectedItem(animal.getEspecie());
        nome.setText(animal.getNome());
        idade.setText(Integer.toString(animal.getIdade()));
        raca.setText(animal.getRaca());
        sexo.setSelectedItem(animal.getSexo());
        situacao.setSelectedItem(animal.isDisponivel() ? "Disponível" : "Adotado");
        if (animal instanceof Cachorro) {
            porte.setSelectedItem(((Cachorro) animal).getPorte());
            energia.setSelectedItem(((Cachorro) animal).getEnergia());
        } else if (animal instanceof Gato) {
            pelagem.setSelectedItem(((Gato) animal).getPelagem());
        }
    }

    /** Converte os valores da tela em um Animal; erros mantêm o formulário aberto. */
    private void validarSalvar() {
        try {
            int anos = Integer.parseInt(idade.getText().trim());
            boolean disponivel = "Disponível".equals(situacao.getSelectedItem());
            int id = original == null ? 0 : original.getId();
            if ("Cachorro".equals(especie.getSelectedItem())) {
                resultado = new Cachorro(id, nome.getText(), anos, raca.getText(),
                        (String) sexo.getSelectedItem(), disponivel,
                        (String) porte.getSelectedItem(), (String) energia.getSelectedItem());
            } else {
                resultado = new Gato(id, nome.getText(), anos, raca.getText(),
                        (String) sexo.getSelectedItem(), disponivel, (String) pelagem.getSelectedItem());
            }
            resultado.setFoto(fotoSalva);
            dispose();
        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this, "A idade deve ser um número inteiro entre 0 e 100.",
                    "Idade inválida", JOptionPane.WARNING_MESSAGE);
            idade.requestFocusInWindow();
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
