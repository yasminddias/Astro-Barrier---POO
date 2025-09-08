package gameobject.path;

import gameobject.IGameObject;
import gameobject.geometry.Point;

/**
 * Implementa uma estratégia de movimento em ziguezague para inimigos.
 * O inimigo alterna entre movimento horizontal e vertical em fases predefinidas,
 * invertendo o padrão ao completar um ciclo e a direção horizontal ao atingir os limites.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv phase é um índice válido para o array durations.
 * @inv phaseTime é o tempo acumulado na fase atual, sempre não negativo.
 * @inv durations contém durações de fase não negativas.
 */
public class ZigZagPath implements EnemyPath {
    private final double horizontalSpeed = 50;
    private final double verticalSpeed = 50;

    private double phaseTime = 0;
    private int phase = 0;
    private boolean forward = true;
    private boolean inverted = false;

    private final double[] durations = {
            1.2,
            1.1,
            4.1,
            1.1,
            1.1
    };

    /**
     * Constrói um ZigZagPath.
     * @post Os parâmetros de controlo de fase (phaseTime, phase, forward, inverted) são inicializados.
     */
    public ZigZagPath() {}

    /**
     * Atualiza a posição do inimigo de acordo com o padrão de movimento em ziguezague.
     * O movimento é dividido em fases com durações específicas.
     * A direção horizontal inverte quando os limites do motor de jogo são atingidos.
     * O padrão de fases (para a frente/para trás) inverte quando todas as fases são completadas.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param go O objeto de jogo (inimigo) a ser movimentado. Não deve ser nulo, e deve estar associado a um motor de jogo com limites (bounds) definidos.
     * @post A posição do IGameObject 'go' é atualizada de acordo com a fase atual do movimento em ziguezague e 'dt'.
     * @post Os contadores de fase (phase, phaseTime) e os indicadores de direção (forward, inverted) são atualizados conforme necessário.
     */
    @Override
    public void update(double dt, IGameObject go) {
        double dx = 0, dy = 0;

        int currentPhase = forward ? phase : durations.length - 1 - phase;

        switch (currentPhase) {
            case 0, 2, 4: dx = getEffectiveHorizontalSpeed(go) * dt; break;
            case 1: dy = (forward ? 1 : -1) * verticalSpeed * dt;   break;
            case 3: dy = (forward ? -1 : 1) * verticalSpeed * dt;   break;
        }

        go.transform().move(new Point(dx, dy), 0);
        phaseTime += dt;

        if (phaseTime >= durations[currentPhase]) {
            phase++;
            phaseTime = 0;

            if (phase >= durations.length) {
                forward = !forward;
                phase = 0;
            }
        }
        // A verificação de limites para inversão horizontal foi movida para getEffectiveHorizontalSpeed
    }

    /**
     * Calcula a velocidade horizontal efetiva, considerando a direção (normal ou invertida)
     * e verificando se a direção precisa ser invertida devido aos limites do motor de jogo.
     * @param go O objeto de jogo (inimigo) cuja posição é usada para verificar os limites. Não deve ser nulo.
     * @return A velocidade horizontal efetiva (pode ser positiva ou negativa).
     * @post O estado 'inverted' pode ser alterado se 'go' atingir um limite horizontal.
     */
    private double getEffectiveHorizontalSpeed(IGameObject go) {
        if (go.engine() != null && go.engine().getBounds() != null) {
            double x = go.transform().position().getX();
            double minX = go.engine().getBounds().getMinX();
            double maxX = go.engine().getBounds().getMaxX();
            // Verifica se é necessário inverter a direção
            if ((!inverted && x >= maxX -1) || (inverted && x <= minX +1 )) { // -1 e +1 para evitar que fique preso
                inverted = !inverted;
            }
        }
        return inverted ? -horizontalSpeed : horizontalSpeed;
    }
}