package adotapet.tela;

import adotapet.excecao.PetException;
import adotapet.modelo.Animal;
import adotapet.modelo.Cachorro;
import adotapet.modelo.Gato;
import adotapet.modelo.Usuario;
import adotapet.servico.GerenciadorPets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Painel compartilhado: operações liberadas pelo polimorfismo de Usuario. */
public class PainelCatalogo extends JPanel {
    private final GerenciadorPets gerenciador;
    private final Usuario usuario;
    private final ModeloTabelaPets modelo = new ModeloTabelaPets();
    private final JTable tabela = new JTable(modelo);
    private final JTextField pesquisa = TemaUI.campoPesquisa("Buscar por nome, raça ou ID...");
    private final JComboBox<String> especie = new JComboBox<String>(new String[]{"Todas as espécies", "Cachorro", "Gato"});
    private final JComboBox<String> situacao = new JComboBox<String>(new String[]{"Todos os status", "Disponíveis", "Adotados"});
    private final JLabel estatTotal = TemaUI.texto("0", 27, true, TemaUI.MARROM_CACAU);
    private final JLabel estatDisponiveis = TemaUI.texto("0", 27, true, TemaUI.TERRACOTA);
    private final JLabel estatAdotados = TemaUI.texto("0", 27, true, TemaUI.MARROM_CACAU);
    private final JLabel resultados = TemaUI.texto("", 12, false, TemaUI.SECUNDARIO);
    private final JLabel tituloDetalhes = TemaUI.texto("Selecione um animal", 19, true, TemaUI.MARROM_CACAU);
    private final JLabel imagemDetalhes = new JLabel();
    private final JLabel textoDetalhes = TemaUI.texto("Veja as informações do pet selecionado.", 12, false, TemaUI.SECUNDARIO);
    private final JTextArea cuidados = new JTextArea();
    private final JPanel painelEspecificos = TemaUI.painelTransparente(new BorderLayout());
    private final Runnable aoSair;

    public PainelCatalogo(GerenciadorPets gerenciador, Usuario usuario, Runnable aoSair) {
        this.gerenciador = gerenciador;
        this.usuario = usuario;
        this.aoSair = aoSair;
        setLayout(new BorderLayout());
        setBackground(TemaUI.FUNDO);
        montar();
        registrarAcoes();
        atualizar(-1);
    }

    /** Organiza cabeçalho, indicadores, lista de animais e ficha de detalhes. */
    private void montar() {
        add(cabecalho(), BorderLayout.NORTH);
        JPanel area = new JPanel(new BorderLayout(0, 14));
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));
        area.add(estatisticas(), BorderLayout.NORTH);
        area.add(conteudoCentral(), BorderLayout.CENTER);
        add(area, BorderLayout.CENTER);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(TemaUI.FUNDO);
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 27, 13, 27));
        rodape.add(TemaUI.texto("AdotaPet  •  sistema local para ONG  •  dados em TXT", 11, false, TemaUI.SECUNDARIO), BorderLayout.WEST);
        rodape.add(TemaUI.texto("LPOO  |  Java + Swing", 11, false, TemaUI.SECUNDARIO), BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel cabecalho() {
        JPanel painel = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setPaint(new GradientPaint(0, 0, TemaUI.AMARELO_MEL, getWidth(), 0, new Color(255, 221, 151)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(19, 27, 19, 27));
        JPanel nomes = TemaUI.painelTransparente(new BorderLayout(0, 2));
        nomes.add(TemaUI.texto("AdotaPet  ♥", 27, true, TemaUI.MARROM_CACAU), BorderLayout.NORTH);
        nomes.add(TemaUI.texto(usuario.podeGerenciar() ? "Cuidando de cada patinha com carinho" :
                "Encontre um novo amigo para sua família", 13, false, TemaUI.MARROM_CACAU), BorderLayout.SOUTH);
        painel.add(nomes, BorderLayout.WEST);
        JPanel direita = TemaUI.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 13, 7));
        JLabel papel = TemaUI.texto(usuario.podeGerenciar() ? "ADMINISTRADOR" : "VISITANTE", 12, true, TemaUI.MARROM_CACAU);
        direita.add(papel);
        JButton sair = TemaUI.botao("Voltar ao início", false);
        sair.addActionListener(e -> aoSair.run());
        direita.add(sair);
        painel.add(direita, BorderLayout.EAST);
        return painel;
    }

    private JPanel estatisticas() {
        JPanel painel = TemaUI.painelTransparente(new GridLayout(1, 3, 13, 0));
        if (usuario.podeGerenciar()) {
            painel.add(cartaoEstatistica("ANIMAIS CADASTRADOS", estatTotal, "Todos os registros"));
            painel.add(cartaoEstatistica("DISPONÍVEIS", estatDisponiveis, "Aguardando uma família"));
            painel.add(cartaoEstatistica("ADOTADOS", estatAdotados, "Já encontraram um lar"));
        } else {
            painel.add(cartaoEstatistica("PETS PARA ADOÇÃO", estatTotal, "Animais disponíveis"));
            painel.add(cartaoEstatistica("CACHORROS", estatDisponiveis, "Para conhecer e adotar"));
            painel.add(cartaoEstatistica("GATOS", estatAdotados, "Para conhecer e adotar"));
        }
        return painel;
    }

    private JPanel cartaoEstatistica(String rotulo, JLabel valor, String legenda) {
        TemaUI.Cartao cartao = new TemaUI.Cartao(new BorderLayout(0, 6));
        TemaUI.margem(cartao, 14, 18, 14, 18);
        cartao.add(TemaUI.texto(rotulo, 11, true, TemaUI.SECUNDARIO), BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        cartao.add(TemaUI.texto(legenda, 11, false, TemaUI.SECUNDARIO), BorderLayout.SOUTH);
        return cartao;
    }

    private JPanel conteudoCentral() {
        JPanel painel = TemaUI.painelTransparente(new BorderLayout(14, 0));
        painel.add(listaAnimais(), BorderLayout.CENTER);
        JPanel detalhes = detalhes();
        detalhes.setPreferredSize(new Dimension(276, 390));
        painel.add(detalhes, BorderLayout.EAST);
        return painel;
    }

    /** Monta os filtros e a tabela; botões de alteração só existem para administradores. */
    private JPanel listaAnimais() {
        TemaUI.Cartao cartao = new TemaUI.Cartao(new BorderLayout(0, 12));
        TemaUI.margem(cartao, 16, 17, 16, 17);
        JPanel parteSuperior = TemaUI.painelTransparente(new BorderLayout(0, 12));
        JPanel titulo = TemaUI.painelTransparente(new BorderLayout());
        titulo.add(TemaUI.texto(usuario.podeGerenciar() ? "Cadastro de animais" :
                "Pets disponíveis", 19, true, TemaUI.MARROM_CACAU), BorderLayout.WEST);
        titulo.add(resultados, BorderLayout.EAST);
        parteSuperior.add(titulo, BorderLayout.NORTH);
        JPanel filtros = TemaUI.painelTransparente(new BorderLayout(8, 0));
        pesquisa.setToolTipText("Pesquise por nome, raça, espécie ou ID (sem diferenciar acentos)");
        filtros.add(pesquisa, BorderLayout.CENTER);
        especie.setPreferredSize(new Dimension(158, 39));
        especie.setFont(TemaUI.fonte(Font.PLAIN, 12));
        filtros.add(especie, BorderLayout.EAST);
        parteSuperior.add(filtros, BorderLayout.CENTER);
        if (usuario.podeGerenciar()) {
            JPanel filtrosAdmin = TemaUI.painelTransparente(new BorderLayout(8, 0));
            situacao.setPreferredSize(new Dimension(165, 35));
            situacao.setFont(TemaUI.fonte(Font.PLAIN, 12));
            filtrosAdmin.add(situacao, BorderLayout.WEST);
            filtrosAdmin.add(TemaUI.texto("Selecione um pet na tabela para editar ou ver detalhes.",
                    11, false, TemaUI.SECUNDARIO), BorderLayout.CENTER);
            parteSuperior.add(filtrosAdmin, BorderLayout.SOUTH);
        } else {
            parteSuperior.add(TemaUI.texto("Somente animais disponíveis para adoção são exibidos.",
                    11, false, TemaUI.SECUNDARIO), BorderLayout.SOUTH);
        }
        cartao.add(parteSuperior, BorderLayout.NORTH);
        configurarTabela();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDA));
        scroll.getViewport().setBackground(Color.WHITE);
        cartao.add(scroll, BorderLayout.CENTER);

        JPanel linhaInferior = TemaUI.painelTransparente(new BorderLayout(8, 0));
        if (usuario.podeGerenciar()) {
            JPanel botoes = TemaUI.painelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton novo = TemaUI.botao("+ Novo pet", true);
            JButton editar = TemaUI.botao("Editar", false);
            JButton status = TemaUI.botao("Alterar status", false);
            JButton excluir = TemaUI.botao("Excluir", false);
            novo.addActionListener(e -> cadastrar());
            editar.addActionListener(e -> editar());
            status.addActionListener(e -> alterarStatus());
            excluir.addActionListener(e -> excluir());
            botoes.add(novo);
            botoes.add(editar);
            botoes.add(status);
            botoes.add(excluir);
            linhaInferior.add(botoes, BorderLayout.WEST);
        } else {
            linhaInferior.add(TemaUI.texto("Gostou de algum? Converse com a ONG sobre a adoção responsável.",
                    12, false, TemaUI.SECUNDARIO), BorderLayout.WEST);
        }
        cartao.add(linhaInferior, BorderLayout.SOUTH);
        return cartao;
    }

    /** Configura aparência, seleção e atalhos da tabela compartilhada entre os perfis. */
    private void configurarTabela() {
        tabela.setFont(TemaUI.fonte(Font.PLAIN, 12));
        tabela.setForeground(TemaUI.TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(39);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(255, 229, 161));
        tabela.setSelectionForeground(TemaUI.MARROM_CACAU);
        tabela.setFillsViewportHeight(true);
        JTableHeader header = tabela.getTableHeader();
        header.setFont(TemaUI.fonte(Font.BOLD, 12));
        header.setForeground(TemaUI.MARROM_CACAU);
        header.setBackground(new Color(255, 237, 198));
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tab, Object valor,
                    boolean selecionado, boolean foco, int linha, int coluna) {
                super.getTableCellRendererComponent(tab, valor, selecionado, foco, linha, coluna);
                setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
                if (!selecionado) {
                    setBackground(linha % 2 == 0 ? Color.WHITE : new Color(255, 249, 238));
                }
                if (coluna == 6) {
                    setForeground("Disponível".equals(valor) ? TemaUI.TERRACOTA : TemaUI.SECUNDARIO);
                    setFont(TemaUI.fonte(Font.BOLD, 12));
                } else {
                    setForeground(TemaUI.TEXTO);
                    setFont(TemaUI.fonte(Font.PLAIN, 12));
                }
                return this;
            }
        };
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        tabela.getColumnModel().getColumn(0).setPreferredWidth(42);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(115);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(82);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(67);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(85);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhes(getSelecionado());
            }
        });
        tabela.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "detalhes");
        tabela.getActionMap().put("detalhes", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetalhes(getSelecionado());
            }
        });
    }

    private JPanel detalhes() {
        TemaUI.Cartao cartao = new TemaUI.Cartao(new BorderLayout(0, 10));
        TemaUI.margem(cartao, 19, 18, 18, 18);
        JPanel topo = TemaUI.painelTransparente(new BorderLayout(0, 8));
        topo.add(TemaUI.texto("FICHA DO ANIMAL", 11, true, TemaUI.TERRACOTA), BorderLayout.NORTH);
        topo.add(tituloDetalhes, BorderLayout.CENTER);
        imagemDetalhes.setPreferredSize(new Dimension(222, 130));
        imagemDetalhes.setBorder(BorderFactory.createLineBorder(TemaUI.BORDA));
        ImagemPet.mostrar(imagemDetalhes, null, 220, 128);
        topo.add(imagemDetalhes, BorderLayout.SOUTH);
        cartao.add(topo, BorderLayout.NORTH);
        JPanel conteudo = TemaUI.painelTransparente(new BorderLayout(0, 12));
        textoDetalhes.setVerticalAlignment(SwingConstants.TOP);
        conteudo.add(textoDetalhes, BorderLayout.NORTH);
        JLabel subtitulo = TemaUI.texto("CUIDADOS ESPECIAIS", 11, true, TemaUI.SECUNDARIO);
        JPanel secao = TemaUI.painelTransparente(new BorderLayout(0, 7));
        secao.add(subtitulo, BorderLayout.NORTH);
        cuidados.setText("Selecione um animal para saber mais.");
        cuidados.setFont(TemaUI.fonte(Font.PLAIN, 13));
        cuidados.setForeground(TemaUI.TEXTO);
        cuidados.setLineWrap(true);
        cuidados.setWrapStyleWord(true);
        cuidados.setEditable(false);
        cuidados.setFocusable(false);
        cuidados.setOpaque(false);
        cuidados.setMargin(new Insets(0, 0, 0, 0));
        JScrollPane rolagemCuidados = new JScrollPane(cuidados);
        rolagemCuidados.setBorder(null);
        rolagemCuidados.setOpaque(false);
        rolagemCuidados.getViewport().setOpaque(false);
        rolagemCuidados.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagemCuidados.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        secao.add(rolagemCuidados, BorderLayout.CENTER);
        conteudo.add(secao, BorderLayout.CENTER);
        cartao.add(conteudo, BorderLayout.CENTER);
        painelEspecificos.add(TemaUI.texto("Adoção é um compromisso de cuidado.",
                11, false, TemaUI.SECUNDARIO), BorderLayout.CENTER);
        cartao.add(painelEspecificos, BorderLayout.SOUTH);
        return cartao;
    }

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    /** Atualiza a ficha e os cuidados específicos da espécie selecionada. */
    private void mostrarDetalhes(Animal animal) {
        if (animal == null) {
            tituloDetalhes.setText("Selecione um animal");
            ImagemPet.mostrar(imagemDetalhes, null, 220, 128);
            textoDetalhes.setText("Veja as informações do pet selecionado.");
            cuidados.setText("Selecione um animal para saber mais.");
            cuidados.setCaretPosition(0);
            return;
        }
        tituloDetalhes.setText(animal.getNome());
        ImagemPet.mostrar(imagemDetalhes, animal.getFoto().isEmpty() ? null :
                gerenciador.getPastaFotos().resolve(animal.getFoto()), 220, 128);
        String extra;
        if (animal instanceof Cachorro) {
            Cachorro cao = (Cachorro) animal;
            extra = "<b>Porte:</b> " + escaparHtml(cao.getPorte()) + "<br><b>Energia:</b> " + escaparHtml(cao.getEnergia());
        } else {
            extra = "<b>Pelagem:</b> " + escaparHtml(((Gato) animal).getPelagem());
        }
        textoDetalhes.setText("<html><body style='width:220px;line-height:1.5'>"
                + "<b>ID:</b> #" + animal.getId() + "<br>"
                + "<b>Espécie:</b> " + animal.getEspecie() + "<br>"
                + "<b>Raça:</b> " + escaparHtml(animal.getRaca()) + "<br>"
                + "<b>Idade:</b> " + animal.getIdade() + " ano(s)<br>"
                + "<b>Sexo:</b> " + animal.getSexo() + "<br>"
                + "<b>Situação:</b> " + animal.getSituacao() + "<br>"
                + extra + "</body></html>");
        cuidados.setText(animal.getCuidadosEspeciais()); // chamada polimórfica
        cuidados.setCaretPosition(0);
    }

    /** Atualiza a listagem quando o usuário digita na busca ou altera os filtros. */
    private void registrarAcoes() {
        pesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizar(-1);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizar(-1);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizar(-1);
            }
        });
        especie.addActionListener(e -> atualizar(-1));
        situacao.addActionListener(e -> atualizar(-1));
    }

    /** Reconsulta o serviço, atualiza indicadores e tenta preservar a seleção atual. */
    private void atualizar(int selecionarId) {
        try {
            int idAntes = selecionarId > 0 ? selecionarId : getSelecionadoId();
            String filtroEspecie = "Todas as espécies".equals(especie.getSelectedItem())
                    ? "" : (String) especie.getSelectedItem();
            String filtroSituacao = usuario.podeGerenciar() ? (String) situacao.getSelectedItem() : "Disponíveis";
            List<Animal> lista = gerenciador.listar(usuario, pesquisa.getText(), filtroEspecie, filtroSituacao);
            modelo.atualizar(lista);
            if (usuario.podeGerenciar()) {
                estatTotal.setText(String.valueOf(gerenciador.getTotal()));
                estatDisponiveis.setText(String.valueOf(gerenciador.getTotalDisponiveis()));
                estatAdotados.setText(String.valueOf(gerenciador.getTotal() - gerenciador.getTotalDisponiveis()));
            } else {
                estatTotal.setText(String.valueOf(gerenciador.getTotalDisponiveis()));
                estatDisponiveis.setText(String.valueOf(gerenciador.contarDisponiveisPorEspecie("Cachorro")));
                estatAdotados.setText(String.valueOf(gerenciador.contarDisponiveisPorEspecie("Gato")));
            }
            resultados.setText(lista.size() + (lista.size() == 1 ? " resultado" : " resultados"));
            if (idAntes > 0) {
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getId() == idAntes) {
                        tabela.setRowSelectionInterval(i, i);
                        return;
                    }
                }
            }
            tabela.clearSelection();
            mostrarDetalhes(null);
        } catch (PetException erro) {
            exibirErro(erro);
        }
    }

    private int getSelecionadoId() {
        Animal animal = getSelecionado();
        return animal == null ? -1 : animal.getId();
    }

    private Animal getSelecionado() {
        int linha = tabela.getSelectedRow();
        return modelo.getAnimal(linha);
    }

    private Animal exigirSelecao() {
        Animal animal = getSelecionado();
        if (animal == null) {
            JOptionPane.showMessageDialog(this, "Selecione um animal na tabela primeiro.",
                    "Nenhum animal selecionado", JOptionPane.INFORMATION_MESSAGE);
        }
        return animal;
    }

    /** Solicita os dados do formulário e delega cadastro e gravação ao serviço. */
    private void cadastrar() {
        DialogoPet.Resultado formulario = DialogoPet.exibir(this, null, gerenciador.getPastaFotos());
        if (formulario == null) {
            return;
        }
        try {
            Animal cadastrado = gerenciador.cadastrar(usuario, formulario.getAnimal(), formulario.getNovaFoto());
            atualizar(cadastrado.getId());
            JOptionPane.showMessageDialog(this, "Pet cadastrado com sucesso! ID #" + cadastrado.getId(),
                    "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
        } catch (PetException | IOException | IllegalArgumentException erro) { exibirErro(erro); }
    }

    private void editar() {
        Animal selecionado = exigirSelecao();
        if (selecionado == null) {
            return;
        }
        DialogoPet.Resultado formulario = DialogoPet.exibir(this, selecionado, gerenciador.getPastaFotos());
        if (formulario == null) {
            return;
        }
        try {
            gerenciador.editar(usuario, selecionado.getId(), formulario.getAnimal(), formulario.getNovaFoto());
            atualizar(selecionado.getId());
            JOptionPane.showMessageDialog(this, "Cadastro atualizado com sucesso.",
                    "Alterações salvas", JOptionPane.INFORMATION_MESSAGE);
        } catch (PetException | IOException | IllegalArgumentException erro) { exibirErro(erro); }
    }

    /** Confirma a troca de situação para evitar alterações acidentais. */
    private void alterarStatus() {
        Animal selecionado = exigirSelecao();
        if (selecionado == null) {
            return;
        }
        boolean novoStatus = !selecionado.isDisponivel();
        String mensagem = novoStatus ? "Marcar '" + selecionado.getNome() + "' como disponível para adoção?"
                : "Marcar '" + selecionado.getNome() + "' como adotado?";
        int resposta = JOptionPane.showConfirmDialog(this, mensagem, "Confirmar alteração",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            gerenciador.alterarSituacao(usuario, selecionado.getId(), novoStatus);
            atualizar(selecionado.getId());
        } catch (PetException | IOException erro) { exibirErro(erro); }
    }

    private void excluir() {
        Animal selecionado = exigirSelecao();
        if (selecionado == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir permanentemente o animal '" + selecionado.getNome() + "' (ID #"
                + selecionado.getId() + ")?\nEsta ação não poderá ser desfeita.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            gerenciador.excluir(usuario, selecionado.getId());
            atualizar(-1);
            JOptionPane.showMessageDialog(this, "Animal excluído do cadastro.",
                    "Exclusão concluída", JOptionPane.INFORMATION_MESSAGE);
        } catch (PetException | IOException erro) { exibirErro(erro); }
    }

    private void exibirErro(Exception erro) {
        JOptionPane.showMessageDialog(this,
                "A operação não foi concluída.\n" + erro.getMessage()
                + "\n\nOs dados anteriores foram preservados quando possível.",
                "Problema no AdotaPet", JOptionPane.ERROR_MESSAGE);
    }
}
