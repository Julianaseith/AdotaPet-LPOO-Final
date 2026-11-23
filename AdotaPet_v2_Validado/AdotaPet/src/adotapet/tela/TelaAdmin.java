package adotapet.tela;

import adotapet.modelo.Administrador;
import adotapet.servico.GerenciadorPets;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Janela de gestão, disponível apenas após autenticação do administrador. */
public class TelaAdmin extends javax.swing.JFrame {
    public TelaAdmin(GerenciadorPets gerenciador, Administrador usuario) {
        initComponents();
        setTitle("AdotaPet | Administração");
        setSize(1200, 760);
        setMinimumSize(new Dimension(1040, 660));
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
