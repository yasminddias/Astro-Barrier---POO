package gameobject.transform;

import gameobject.geometry.Point;

/**
 * Interface que define as operações básicas de transformação aplicáveis a objetos de jogo.
 * Inclui movimentação, rotação, escalonamento e acesso à posição, camada e ângulo atuais.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O ponto de posição nunca é nulo.
 * @inv A escala (scale) é sempre maior ou igual a 0.
 */
public interface ITransform {

    /**
     * Move a transformação aplicando um deslocamento e uma variação de camada.
     * @param dPos Vetor de deslocamento (dx, dy) a ser aplicado à posição atual. Não deve ser nulo.
     * @param dLayer Variação a ser aplicada à camada atual (pode ser positiva ou negativa).
     * @post A posição da transformação é atualizada pela adição de dPos.
     * @post A camada da transformação é atualizada pela adição de dLayer.
     */
    void move(Point dPos, int dLayer);

    /**
     * Rotaciona a transformação pelo ângulo fornecido (em graus).
     * @param dTheta Ângulo de rotação em graus a ser adicionado ao ângulo atual (positivo para sentido horário, por convenção comum, embora a implementação possa variar).
     * @post O ângulo da transformação é incrementado pelo valor de dTheta.
     */
    void rotate(double dTheta);

    /**
     * Aplica uma variação de escala à transformação.
     * Este método geralmente adiciona o valor dScale à escala atual.
     * @param dScale Valor da variação de escala a ser somado à escala atual (ex: 0.1 para aumentar 10%, -0.2 para diminuir 20% do valor original se a escala fosse 1). A interpretação exata depende da implementação.
     * @post O fator de escala da transformação é ajustado. Se a intenção é uma escala relativa, scale = scale + dScale.
     */
    void scale(double dScale);

    /**
     * Devolve a posição atual da transformação.
     * @return Um objeto Point representando a posição (x, y) atual. Nunca é nulo.
     */
    Point position();

    /**
     * Devolve a camada (layer) atual da transformação.
     * A camada é frequentemente usada para determinar a ordem de renderização (z-index).
     * @return O valor inteiro da camada atual.
     */
    int layer();

    /**
     * Devolve o ângulo atual da transformação, em graus.
     * @return O ângulo de rotação atual em graus.
     */
    double angle();

    /**
     * Devolve o fator de escala atual.
     * @return O valor do fator de escala atual (deve ser >= 0).
     */
    double scale();
}