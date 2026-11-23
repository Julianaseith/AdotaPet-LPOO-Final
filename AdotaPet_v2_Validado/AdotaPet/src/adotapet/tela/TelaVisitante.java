package adotapet.tela;

import adotapet.modelo.Visitante;
import adotapet.servico.GerenciadorPets;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Janela pública sem ações administrativas. */
public class TelaVisitante extends javax.swing.JFrame {
    public TelaVisitante(GerenciadorPets gerenciador, Visitante usuario) {
        initComponents();
        setTitle("AdotaPet | Pets disponíveis");
        setSize(1120, 730);
        setMinimumSize(new Dimension(990, 630));
        setLocationRelativeTo(null);
        conteudo.add(new PainelCatalogo(gerenciador, usuario, () -> dispose()), BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        conteudo = new javax.swing.JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        conteudo.setLayout(new java.awt.BorderLayout());
        getContentPane().add(conteudo, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel conteudo;
    // End of variables declaration//GEN-END:variables
}
