package gameobject.path;

import gameobject.IGameObject;
import gameobject.geometry.Point;
import gameobject.transform.ITransform;

import java.awt.Rectangle;

/**
 * Implementa uma estratégia de movimento retangular para inimigos.
 * O inimigo move-se ao longo dos vértices de um retângulo definido.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv corners é um array de 4 Points representando os vértices do retângulo.
 * @inv currentTargetIndex é um índice válido para o array corners (0 a 3).
 * @inv speed é sempre não negativo.
 */
public class RectangularPath implements EnemyPath {
    private Point[] corners;
    private int currentTargetIndex;
    private double speed;
    private static final double CLOSE_ENOUGH_THRESHOLD = 1.5;

    /**
     * Constrói um RectangularPath.
     * @param rect O retângulo que define o caminho. As suas dimensões e posição são usadas para calcular os vértices. Não deve ser nulo.
     * @param speed A velocidade de movimento ao longo do caminho. O valor absoluto será usado.
     * @post Os vértices (corners) do caminho são definidos com base no 'rect' fornecido.
     * @post currentTargetIndex é inicializado para 0.
     * @post A velocidade (speed) é definida como o valor absoluto do 'speed' fornecido.
     */
    public RectangularPath(Rectangle rect, double speed) {
        this.speed = Math.abs(speed);
        this.corners = new Point[4];
        this.corners[0] = new Point(rect.x, rect.y);
        this.corners[1] = new Point(rect.x + rect.width, rect.y);
        this.corners[2] = new Point(rect.x + rect.width, rect.y + rect.height);
        this.corners[3] = new Point(rect.x, rect.y + rect.height);
        this.currentTargetIndex = 0;
    }

    /**
     * Atualiza a posição do inimigo, movendo-o em direção ao próximo vértice do caminho retangular.
     * Quando um vértice é alcançado (dentro de um limiar), o alvo muda para o próximo vértice.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param go O objeto de jogo (inimigo) a ser movimentado. Não deve ser nulo e deve ter um transform válido.
     * @post A posição do IGameObject 'go' é atualizada na direção do vértice alvo atual.
     * @post Se o vértice alvo for alcançado, currentTargetIndex é atualizado para o próximo vértice no caminho.
     */
    @Override
    public void update(double dt, IGameObject go) {
        if (go == null || corners == null || corners.length == 0) {
            return;
        }

        ITransform transform = go.transform();
        Point currentPosition = transform.position();
        Point targetPosition = corners[currentTargetIndex];

        double dx = targetPosition.getX() - currentPosition.getX();
        double dy = targetPosition.getY() - currentPosition.getY();
        double distanceToTarget = Math.sqrt(dx * dx + dy * dy);

        if (distanceToTarget < CLOSE_ENOUGH_THRESHOLD) {
            transform.position().set(targetPosition.getX(), targetPosition.getY()); // Garante que chega exatamente ao canto
            currentTargetIndex = (currentTargetIndex + 1) % corners.length;
        } else {
            double moveDistance = speed * dt;
            double moveX = (dx / distanceToTarget) * moveDistance;
            double moveY = (dy / distanceToTarget) * moveDistance;
            transform.move(new Point(moveX, moveY), 0);
        }
    }
}