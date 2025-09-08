package gamelevel;

import engine.GameEngine;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

/**
 * Interface que define a estrutura base para todos os níveis do jogo.
 * Cada nível é responsável por carregar os seus próprios objetos de jogo (inimigos, obstáculos, etc.)
 * no motor de jogo e por fornecer informações sobre elementos decorativos específicos do nível.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Qualquer implementação desta interface deve ser capaz de popular um GameEngine com objetos de jogo.
 */
public interface Level {
    /**
     * Carrega os objetos de jogo específicos deste nível no motor de jogo fornecido.
     * Isto inclui inimigos, obstáculos e quaisquer outros elementos relevantes para o nível.
     * @param engine O GameEngine onde os objetos do nível serão carregados. Não deve ser nulo.
     * @post O 'engine' é populado com os IGameObjects definidos para este nível.
     */
    void load(GameEngine engine);

    /**
     * Devolve uma lista das posições Y para as linhas decorativas azuis a serem desenhadas neste nível.
     * A implementação padrão devolve uma lista vazia, indicando que não há linhas azuis por defeito.
     * @return Uma lista de inteiros representando as coordenadas Y das linhas azuis. Pode ser vazia.
     */
    default List<Integer> getBlueLineYPositions() {
        return Collections.emptyList();
    }

    /**
     * Devolve uma lista das posições Y para as linhas decorativas em ziguezague a serem desenhadas neste nível.
     * A implementação padrão devolve uma lista vazia, indicando que não há linhas em ziguezague por defeito.
     * @return Uma lista de inteiros representando as coordenadas Y das linhas em ziguezague. Pode ser vazia.
     */
    default List<Integer> getZigZagLineYPositions() {
        return Collections.emptyList();
    }

    /**
     * Devolve uma lista de posições Y para linhas decorativas personalizadas.
     * A implementação padrão devolve uma lista vazia.
     * @return Uma lista de inteiros representando as coordenadas Y de linhas personalizadas. Pode ser vazia.
     */
    default List<Integer> getCustomLineYPositions() {
        return Collections.emptyList();
    }

    /**
     * Devolve uma lista de retângulos que representam caminhos ou áreas a serem desenhadas,
     * especificamente útil para níveis como o Nível 3 que usam caminhos retangulares para inimigos.
     * A implementação padrão devolve uma lista vazia.
     * @return Uma lista de objetos Rectangle para desenho. Pode ser vazia.
     */
    default List<Rectangle> getPathRectanglesForDrawing() {
        return Collections.emptyList();
    }
}