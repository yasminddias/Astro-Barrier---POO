package gameobject.transform;

import gameobject.geometry.Point;

/**
 * Representa uma transformação espacial aplicada a um GameObject.
 * Inclui posição bidimensional, camada (layer), ângulo de rotação e fator de escala.
 * Utilizada para calcular como o objeto será visualmente posicionado e transformado no jogo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv position nunca é nulo.
 * @inv scale é sempre maior ou igual a 0 (embora o construtor não imponha estritamente >=0, as operações de escala devem manter esta invariante se pretendido).
 */
public class Transform implements ITransform {
    private final Point position;
    private int layer;
    private double angle;
    private double scale;

    /**
     * Construtor da classe Transform.
     * @param x Coordenada x da posição inicial.
     * @param y Coordenada y da posição inicial.
     * @param layer Camada (z-index) do objeto.
     * @param angle Ângulo inicial de rotação (em graus).
     * @param scale Fator inicial de escala. Deve ser >= 0 para evitar comportamentos indefinidos de renderização.
     * @post A posição, camada, ângulo e escala são inicializados conforme os valores fornecidos. position é um novo Point(x,y).
     */
    public Transform(double x, double y, int layer, double angle, double scale) {
        this.position = new Point(x, y);
        this.layer = layer;
        this.angle = angle;
        this.scale = scale;
    }

    /**
     * Move a posição do objeto e altera a camada.
     * @param dPos Vetor de deslocamento (dx, dy) a ser adicionado à posição atual. Não deve ser nulo.
     * @param dLayer Incremento da camada (positivo ou negativo).
     * @post A posição é transladada de acordo com dPos e a camada é ajustada por dLayer.
     */
    @Override
    public void move(Point dPos, int dLayer) {
        position.translate(dPos.getX(), dPos.getY());
        layer += dLayer;
    }

    /**
     * Rotaciona o objeto pelo ângulo especificado.
     * @param dTheta Ângulo de rotação em graus (incremental) a ser adicionado ao ângulo atual.
     * @post O ângulo é incrementado pelo valor fornecido.
     */
    @Override
    public void rotate(double dTheta) {
        angle += dTheta;
    }

    /**
     * Altera o fator de escala do objeto, adicionando dScale ao valor atual.
     * @param dScale Valor a ser somado à escala atual (pode ser negativo, mas o resultado final da escala deve idealmente permanecer >=0).
     * @post O novo fator de escala é ajustado pela adição de dScale.
     */
    @Override
    public void scale(double dScale) {
        scale += dScale;
    }

    /**
     * Devolve a posição atual do objeto.
     * @return Ponto representando a posição (x, y). Nunca é nulo.
     */
    @Override
    public Point position() {
        return position;
    }

    /**
     * Devolve a camada atual do objeto.
     * @return Valor inteiro da camada (layer).
     */
    @Override
    public int layer() {
        return layer;
    }

    /**
     * Devolve o ângulo atual de rotação.
     * @return Ângulo em graus.
     */
    @Override
    public double angle() {
        return angle;
    }

    /**
     * Devolve o fator atual de escala.
     * @return Valor da escala (idealmente >= 0).
     */
    @Override
    public double scale() {
        return scale;
    }
}