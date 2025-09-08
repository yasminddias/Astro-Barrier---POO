package gameobject.shape;

import javax.swing.*;
import java.awt.*;

/**
 * Implementação de IShape que renderiza um objeto de jogo como uma imagem.
 * A imagem é carregada a partir de um caminho de ficheiro e pode ser rotacionada e escalonada.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A imagem (image) pode ser nula se o caminho for inválido ou o ficheiro não for encontrado.
 */
public class ShapeImage implements IShape {
    private final Image image;

    /**
     * Constrói uma ShapeImage carregando uma imagem a partir do caminho especificado.
     * @param path O caminho para o ficheiro da imagem. Não deve ser nulo.
     * @post A imagem é carregada a partir do 'path'. Se o carregamento falhar, 'image' pode ser nula ou conter uma imagem de erro, dependendo do comportamento de ImageIcon.
     */
    public ShapeImage(String path) {
        this.image = new ImageIcon(path).getImage();
    }

    /**
     * Renderiza a imagem no contexto gráfico fornecido, aplicando rotação e escala.
     * A imagem é desenhada centrada nas coordenadas (x, y).
     * @param g2 O contexto Graphics2D onde a imagem será desenhada. Não deve ser nulo.
     * @param x A coordenada x do centro da imagem no ecrã.
     * @param y A coordenada y do centro da imagem no ecrã.
     * @param angle O ângulo de rotação da imagem em graus.
     * @param scale O fator de escala da imagem. Deve ser positivo.
     * @param layer A camada de renderização (não utilizada diretamente nesta implementação, mas parte da interface).
     * @post A imagem é desenhada em g2, rotacionada e escalonada, centrada em (x,y). Se a imagem for nula, nada é desenhado.
     */
    @Override
    public void render(Graphics2D g2, int x, int y, double angle, double scale, int layer) {
        if (image == null) return;
        int width = (int)(image.getWidth(null) * scale);
        int height = (int)(image.getHeight(null) * scale);

        int drawX = x - width / 2;
        int drawY = y - height / 2;

        g2.rotate(Math.toRadians(angle), x, y);
        g2.drawImage(image, drawX, drawY, width, height, null);
        g2.rotate(-Math.toRadians(angle), x, y);
    }
}
