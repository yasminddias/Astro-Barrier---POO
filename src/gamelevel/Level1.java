package gamelevel;

import engine.GameEngine;
import gameobject.entity.Enemy;
import gameobject.path.HorizontalPath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do Nível 1 do jogo.
 * Caracteriza-se por carregar um conjunto de inimigos que se movem horizontalmente.
 * As posições Y dos inimigos também são registadas para possível uso em elementos decorativos.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A lista blueLines nunca é nula; contém as posições Y dos inimigos criados neste nível.
 * @inv A lista zigzagLines é sempre uma lista vazia, pois o Nível 1 não utiliza este tipo de decoração.
 */
public class Level1 implements Level {
    private final List<Integer> blueLines = new ArrayList<>(); // Lista para armazenar as posições Y das linhas azuis (decorativas)
    private final List<Integer> zigzagLines = new ArrayList<>(); // Lista para linhas em ziguezague, não usada neste nível

    /**
     * Carrega os inimigos e define as informações para as linhas decorativas azuis do Nível 1.
     * São criados quatro inimigos, cada um com um caminho de movimento horizontal, e posicionados de forma escalonada.
     * @param engine O GameEngine onde os inimigos e outros elementos do nível serão carregados. Não deve ser nulo, e espera-se que os seus limites (bounds) estejam definidos.
     * @post Quatro instâncias de Enemy, cada uma com um HorizontalPath, são adicionadas como objetos ativos ao 'engine'.
     * @post A lista 'blueLines' é preenchida com as coordenadas Y de cada inimigo criado.
     */
    @Override
    public void load(GameEngine engine) {
        Rectangle bounds = engine.getBounds(); // Obtém os limites da área de jogo do motor
        blueLines.clear(); // Limpa quaisquer posições de linhas azuis de uma carga anterior

        for (int i = 0; i < 4; i++) { // Loop para criar 4 inimigos
            // Cálculo das posições X e Y para cada inimigo, de forma escalonada
            double x = bounds.x + 50 + i * 60; //
            double y = bounds.y + 50 + i * 65 + 20; //

            Enemy enemy = new Enemy("enemy" + i, x, y, new HorizontalPath(50.0)); // Cria um novo inimigo com nome, posição e caminho horizontal
            enemy.setEngine(engine); // Associa o motor de jogo ao inimigo
            engine.addEnabled(enemy); // Adiciona o inimigo à lista de objetos ativos do motor

            blueLines.add((int) y); // Adiciona a posição Y do inimigo à lista de linhas azuis
        }
    }

    /**
     * Devolve as posições Y para as linhas decorativas azuis do Nível 1.
     * Estas posições correspondem às alturas onde os inimigos são colocados.
     * @return Uma lista de inteiros representando as coordenadas Y das linhas azuis.
     */
    @Override
    public List<Integer> getBlueLineYPositions() {
        return blueLines; //
    }

    /**
     * Devolve as posições Y para as linhas decorativas em ziguezague.
     * Para o Nível 1, esta lista está sempre vazia, pois não são usadas.
     * @return Uma lista vazia de inteiros.
     */
    @Override
    public List<Integer> getZigZagLineYPositions() {
        return zigzagLines; //
    }
}