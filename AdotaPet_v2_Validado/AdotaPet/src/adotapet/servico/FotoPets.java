package adotapet.servico;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.ImageIO;

/** Importa imagens JPG/PNG, reduz dimensões e copia para dados/fotos/. */
public final class FotoPets {
    private static final long TAMANHO_MAXIMO = 12L * 1024L * 1024L;
    private static final int LADO_MAXIMO = 1200;
    private FotoPets() {
    }

    public static String importar(Path origem, Path pastaFotos, int id) throws IOException {
        if (origem == null || !Files.isRegularFile(origem)) {
            throw new IOException("Selecione uma foto JPG ou PNG existente.");
        }
        String arquivo = origem.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!(arquivo.endsWith(".jpg") || arquivo.endsWith(".jpeg") || arquivo.endsWith(".png"))) {
            throw new IOException("Formato de foto inválido. Utilize JPG ou PNG.");
        }
        if (Files.size(origem) > TAMANHO_MAXIMO) {
            throw new IOException("A foto deve ter no máximo 12 MB.");
        }
        BufferedImage original = ImageIO.read(origem.toFile());
        if (original == null || original.getWidth() <= 0 || original.getHeight() <= 0) {
            throw new IOException("Não foi possível ler a foto selecionada.");
        }
        double escala = Math.min(1.0, (double) LADO_MAXIMO / Math.max(original.getWidth(), original.getHeight()));
        int largura = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int altura = Math.max(1, (int) Math.round(original.getHeight() * escala));
        BufferedImage reduzida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = reduzida.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, largura, altura);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(original, 0, 0, largura, altura, null);
        } finally { g.dispose(); }
        Files.createDirectories(pastaFotos);
        String nome = "pet-" + id + "-" + UUID.randomUUID().toString() + ".png";
        Path destino = pastaFotos.resolve(nome);
        // Não sobrescreve fotos anteriores de outros animais.
        try {
            if (!ImageIO.write(reduzida, "png", destino.toFile())) {
                throw new IOException("Não foi possível gravar a foto em PNG.");
            }
            return nome;
        } catch (IOException erro) {
            Files.deleteIfExists(destino);
            throw erro;
        }
    }
}
