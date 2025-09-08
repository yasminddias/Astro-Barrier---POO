package gameobject.collider;

import gameobject.geometry.Point;

/**
 * Interface que define o contrato para todos os tipos de colisores no jogo.
 * Um colisor é responsável por definir a forma de colisão de um objeto de jogo
 * e por detetar colisões com outros colisores.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Qualquer implementação deve ser capaz de calcular o seu centroide e verificar colisões.
 */
public interface ICollider {
    /**
     * Calcula e devolve o ponto central (centroide) do colisor no espaço do mundo.
     * @return Um objeto Point representando o centroide do colisor.
     */
    Point centroid();

    /**
     * Move o colisor por um vetor de deslocamento.
     * (Nota: Geralmente, o movimento do colisor é gerido pela atualização da sua Transform associada através do método onUpdate.
     * Este método pode ser para manipulação direta ou legado.)
     * @param dPos O vetor de deslocamento (dx, dy) a ser aplicado. Não deve ser nulo.
     * @post A posição do colisor é transladada de acordo com dPos.
     */
    void move(Point dPos);

    /**
     * Rotaciona o colisor por um determinado ângulo em graus.
     * (Nota: Similar ao move, a rotação é geralmente gerida pela Transform associada via onUpdate.)
     * @param dTheta O ângulo de rotação em graus.
     * @post O estado de rotação do colisor é atualizado. Para colisores simétricos como círculos, pode não ter efeito visual/lógico direto na forma.
     */
    void rotate(double dTheta);

    /**
     * Escala o colisor por um fator de escala.
     * (Nota: Similar ao move, a escala é geralmente gerida pela Transform associada via onUpdate.)
     * @param dScale O fator de escala a ser aplicado (ex: 1.1 para aumentar 10%). Se for um incremento, deve ser > -1.
     * @post As dimensões do colisor são ajustadas de acordo com dScale.
     */
    void scale(double dScale);

    /**
     * Atualiza o estado do colisor, tipicamente sincronizando-o com a Transform do GameObject ao qual está associado.
     * Este método deve ser chamado em cada passo do ciclo de jogo antes da deteção de colisões.
     * @post O estado interno do colisor (posição, rotação, escala, vértices transformados, etc.) é atualizado para refletir o estado atual da sua Transform associada.
     */
    void onUpdate();

    /**
     * Verifica se este colisor está a colidir com outro colisor genérico (ICollider).
     * Normalmente implementado usando double dispatch para resolver para tipos específicos de colisores.
     * @param other O outro ICollider com o qual verificar a colisão. Não deve ser nulo.
     * @return Verdadeiro se houver colisão, falso caso contrário.
     */
    boolean isColliding(ICollider other);


    /**
     * Verifica se este colisor está a colidir com um CircleCollider específico.
     * @param other O CircleCollider com o qual verificar a colisão. Não deve ser nulo.
     * @return Verdadeiro se houver colisão, falso caso contrário.
     */
    boolean isColliding(CircleCollider other);


    /**
     * Verifica se este colisor está a colidir com um PolygonCollider específico.
     * @param other O PolygonCollider com o qual verificar a colisão. Não deve ser nulo.
     * @return Verdadeiro se houver colisão, falso caso contrário.
     */
    boolean isColliding(PolygonCollider other);

    /**
     * Devolve uma dimensão característica do colisor.
     * Para um círculo, pode ser o raio. Para um polígono, pode ser metade da largura ou altura da sua bounding box, ou outro valor representativo.
     * @return Um valor double representando uma dimensão característica.
     */
    double getCharacteristicDimension();
}