package gameobject.path;

import gameobject.IGameObject;
import gameobject.geometry.Point;

/**
 * Implementa uma estratégia de movimento horizontal para inimigos.
 * O inimigo move-se horizontalmente e inverte a direção ao atingir os limites do ecrã.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A velocidade (speed) pode mudar de sinal, mas o seu valor absoluto permanece constante, exceto quando explicitamente alterado.
 */
public class HorizontalPath implements EnemyPath {
    private double speed;

    /**
     * Constrói um HorizontalPath com uma velocidade inicial.
     * @param speed A velocidade horizontal inicial. Positiva para a direita, negativa para a esquerda.
     * @post A velocidade do caminho é inicializada com o valor fornecido.
     */
    public HorizontalPath(double speed) {
        this.speed = speed;
    }

    /**
     * Atualiza a posição do inimigo, movendo-o horizontalmente.
     * Se o inimigo atingir os limites laterais definidos pelo motor de jogo, a sua direção de movimento é invertida.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param enemy O objeto de jogo (inimigo) a ser movimentado. Não deve ser nulo, e deve estar associado a um motor de jogo com limites (bounds) definidos.
     * @post A posição X do 'enemy' é atualizada com base na velocidade e 'dt'.
     * @post A velocidade (speed) inverte o sinal se o 'enemy' atingir os limites horizontais do motor de jogo.
     */
    @Override
    public void update(double dt, IGameObject enemy) {
        enemy.transform().move(new Point(speed * dt, 0), 0);

        double x = enemy.transform().position().getX();
        if (enemy.engine() != null && enemy.engine().getBounds() != null) {
            double minX = enemy.engine().getBounds().getMinX();
            double maxX = enemy.engine().getBounds().getMaxX();

            // Considera o tamanho do inimigo para uma inversão mais precisa nas bordas
            // Esta lógica pode precisar de ajuste dependendo se 'x' é o centro ou a borda do inimigo
            // e se os limites do motor já consideram o tamanho dos objetos.
            // Assumindo que 'x' é o centro e os limites são para o centro.
            if ((speed > 0 && x >= maxX) || (speed < 0 && x <= minX)) {
                speed = -speed;
            }
        }
    }
}