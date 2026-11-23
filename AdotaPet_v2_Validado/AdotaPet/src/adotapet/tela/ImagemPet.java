package adotapet.tela;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Exibe prévia sem distorcer as proporções de uma foto JPG ou PNG. */
public final class ImagemPet {
    private ImagemPet() {
    }

    public static void mostrar(JLabel rotulo, Path arquivo, int largura, int altura) {
        rotulo.setHorizontalAlignment(SwingConstants.CENTER);
        rotulo.setVerticalAlignment(SwingConstants.CENTER);
        rotulo.setOpaque(true);
        rotulo.setBackground(TemaUI.SUAVE);
        rotulo.setForeground(TemaUI.SECUNDARIO);
        rotulo.setIcon(null);
        rotulo.setText("Sem foto cadastrada");
        if (arquivo == null || !Files.isRegularFile(arquivo)) return;
        try {
            BufferedImage original = ImageIO.read(arquivo.toFile());
            if (original == null) {
                rotulo.setText("Foto indisponível");
                return;
            }
            double escala = Math.min((double) largura / original.getWidth(),
                    (double) altura / original.getHeight());
            int novaLargura = Math.max(1, (int) Math.round(original.getWidth() * escala));
            int novaAltura = Math.max(1, (int) Math.round(original.getHeight() * escala));
            BufferedImage miniatura = new BufferedImage(novaLargura, novaAltura,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = miniatura.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawImage(original, 0, 0, novaLargura, novaAltura, null);
            } finally { g.dispose(); }
            rotulo.setText(null);
            rotulo.setIcon(new ImageIcon(miniatura));
        } catch (IOException | IllegalArgumentException erro) {
            rotulo.setText("Foto indisponível");
        }
    }
}
