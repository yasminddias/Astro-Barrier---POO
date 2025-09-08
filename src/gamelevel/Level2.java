package gamelevel;

import engine.GameEngine;
import gameobject.entity.Enemy;
import gameobject.entity.ObstacleBlock;
import gameobject.path.HorizontalPath;
import gameobject.path.ZigZagPath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do Nível 2 do jogo.
 * Este nível introduz uma maior variedade de comportamentos de inimigos, incluindo
 * um inimigo com movimento horizontal e outro com movimento em ziguezague.
 * Adiciona também um obstáculo estático no centro da área de jogo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv As listas blueLines e zigzagLines nunca são nulas; contêm as posições Y dos elementos decorativos correspondentes.
 */
public class Level2 implements Level {
    private final List<Integer> blueLines = new ArrayList<>(); // Lista para posições Y de linhas azuis
    private final List<Integer> zigzagLines = new ArrayList<>(); // Lista para posições Y de linhas em ziguezague

    /**
     * Carrega os inimigos, o obstáculo e define as informações para as linhas decorativas do Nível 2.
     * Cria um inimigo com movimento horizontal, um inimigo com movimento em ziguezague, e um ObstacleBlock.
     * @param engine O GameEngine onde os objetos do nível serão carregados. Não deve ser nulo, e espera-se que os seus limites (bounds) estejam definidos.
     * @post Um Enemy com HorizontalPath, um Enemy com ZigZagPath e um ObstacleBlock são adicionados como objetos ativos ao 'engine'.
     * @post As listas 'blueLines' e 'zigzagLines' são preenchidas com as coordenadas Y dos respetivos elementos decorativos.
     */
    @Override
    public void load(GameEngine engine) {
        Rectangle bounds = engine.getBounds(); // Obtém os limites da área de jogo
        blueLines.clear(); // Limpa listas de cargas anteriores
        zigzagLines.clear(); // Limpa listas de cargas anteriores

        // Linha azul: inimigo com caminho reto (horizontal)
        int yBlue = bounds.y + 80; // Define a posição Y para o inimigo de movimento horizontal e linha azul
        blueLines.add(yBlue); // Adiciona à lista para desenho da linha

        Enemy straightEnemy = new Enemy("enemy1", bounds.x + 40, yBlue, new HorizontalPath(50.0)); // Cria o inimigo horizontal
        straightEnemy.setEngine(engine); // Associa o motor ao inimigo
        engine.addEnabled(straightEnemy); // Adiciona o inimigo ao motor

        // Linha ziguezague: inimigo com caminho em ziguezague
        int yZigZag = bounds.y + 160; // Define a posição Y para o inimigo em ziguezague e linha correspondente
        zigzagLines.add(yZigZag); // Adiciona à lista para desenho da linha

        int startX = bounds.x;    // Posição X inicial para o inimigo em ziguezague
        // A posição Y inicial do inimigo em ziguezague é ajustada em relação à linha decorativa yZigZag.
        // O valor -26 sugere que o ponto de referência do inimigo (provavelmente o centro)
        // deve começar ligeiramente acima da linha para que o seu movimento subsequente se alinhe visualmente com ela.
        int startY = yZigZag - 26; //

        Enemy zigzagEnemy = new Enemy("enemy2", startX, startY, new ZigZagPath()); // Cria o inimigo em ziguezague
        zigzagEnemy.setEngine(engine); // Associa o motor ao inimigo
        engine.addEnabled(zigzagEnemy); // Adiciona o inimigo ao motor

        // Obstáculo no centro
        // As coordenadas (160, 250) e dimensões (40, 40) são fixas.
        ObstacleBlock block = new ObstacleBlock("obstacle", 160, 250, 40, 40); // Cria o obstáculo
        block.setEngine(engine); // Associa o motor ao obstáculo
        engine.addEnabled(block); // Adiciona o obstáculo ao motor
    }

    /**
     * Devolve as posições Y para as linhas decorativas azuis do Nível 2.
     * @return Uma lista de inteiros representando as coordenadas Y das linhas azuis.
     */
    @Override
    public List<Integer> getBlueLineYPositions() {
        return blueLines; //
    }

    /**
     * Devolve as posições Y para as linhas decorativas em ziguezague do Nível 2.
     * @return Uma lista de inteiros representando as coordenadas Y das linhas em ziguezague.
     */
    @Override
    public List<Integer> getZigZagLineYPositions() {
        return zigzagLines; //
    }
}