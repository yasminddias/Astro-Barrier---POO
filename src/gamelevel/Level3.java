package gamelevel;

import engine.GameEngine;
import gameobject.entity.Enemy;
import gameobject.entity.ObstacleBlock;
import gameobject.path.RectangularPath;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do Nível 3 do jogo.
 * Este nível é caracterizado por inimigos que seguem caminhos retangulares
 * e pela presença de múltiplos obstáculos. As linhas decorativas tradicionais
 * (azuis, ziguezague) não são o foco principal aqui; em vez disso, os próprios
 * caminhos retangulares dos inimigos podem ser usados para fins visuais.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A lista pathRectangles nunca é nula e contém os retângulos que definem os caminhos dos inimigos.
 * @inv As listas blueLines e zigzagLines são geralmente listas vazias, pois este nível não as utiliza da mesma forma que os anteriores.
 */
public class Level3 implements Level {
    private final List<Rectangle> pathRectangles = new ArrayList<>(); // Lista para armazenar os retângulos dos caminhos para desenho

    // Listas para linhas decorativas, geralmente não usadas extensivamente no Nível 3
    private final List<Integer> blueLines = new ArrayList<>(); //
    private final List<Integer> zigzagLines = new ArrayList<>(); //

    /**
     * Carrega os inimigos com caminhos retangulares e os obstáculos para o Nível 3.
     * Configura quatro inimigos, cada um a mover-se num caminho retangular distinto,
     * e dois obstáculos posicionados na parte inferior da área de jogo.
     * @param engine O GameEngine onde os objetos do nível serão carregados. Não deve ser nulo, e espera-se que os seus limites (bounds) estejam definidos.
     * @post Quatro instâncias de Enemy, cada uma com um RectangularPath, e duas instâncias de ObstacleBlock são adicionadas como objetos ativos ao 'engine'.
     * @post A lista 'pathRectangles' é preenchida com os objetos Rectangle que definem os caminhos dos inimigos.
     */
    @Override
    public void load(GameEngine engine) {
        pathRectangles.clear(); // Limpa caminhos de cargas anteriores
        Rectangle gameAreaBounds = engine.getBounds(); // Obtém os limites da área de jogo

        // Configurações dos caminhos retangulares dos inimigos
        int rectWidth = 120; // Largura do retângulo do caminho
        int rectHeight = 70; // Altura do retângulo do caminho
        int marginFromSide = 15; // Margem lateral
        int topRectsInitialMargin = 30; // Margem superior inicial para retângulos de cima
        int topRectsPushDown = 20; // Deslocamento adicional para baixo para retângulos de cima
        int bottomRectsInitialMargin = 30; // Margem inferior inicial para retângulos de baixo (a partir do fundo)
        int bottomRectsExtraPushUp = 120; // Deslocamento adicional para cima para retângulos de baixo (para afastá-los do fundo)
        double enemySpeed = 70.0; // Velocidade dos inimigos

        // 1. Retângulo Superior Esquerdo (Caminho do Inimigo)
        int topLeftY = gameAreaBounds.y + topRectsInitialMargin + topRectsPushDown; //
        Rectangle topLeftRect = new Rectangle( //
                gameAreaBounds.x + marginFromSide, //
                topLeftY, //
                rectWidth, //
                rectHeight //
        );
        pathRectangles.add(topLeftRect); // Adiciona o retângulo à lista para desenho
        Enemy enemyTL = new Enemy("enemy_lvl3_TL", topLeftRect.x, topLeftRect.y, new RectangularPath(topLeftRect, enemySpeed)); // Cria o inimigo
        enemyTL.setEngine(engine); // Associa o motor ao inimigo
        engine.addEnabled(enemyTL); // Adiciona o inimigo ao motor

        // 2. Retângulo Superior Direito (Caminho do Inimigo)
        int topRightY = gameAreaBounds.y + topRectsInitialMargin + topRectsPushDown; //
        Rectangle topRightRect = new Rectangle( //
                gameAreaBounds.x + gameAreaBounds.width - marginFromSide - rectWidth, //
                topRightY, //
                rectWidth, //
                rectHeight //
        );
        pathRectangles.add(topRightRect); //
        Enemy enemyTR = new Enemy("enemy_lvl3_TR", topRightRect.x, topRightRect.y, new RectangularPath(topRightRect, enemySpeed)); // Cria o inimigo
        enemyTR.setEngine(engine); //
        engine.addEnabled(enemyTR); //

        // 3. Retângulo Inferior Esquerdo (Caminho do Inimigo)
        int bottomLeftY = gameAreaBounds.y + gameAreaBounds.height - bottomRectsInitialMargin - rectHeight - bottomRectsExtraPushUp; //
        Rectangle bottomLeftRect = new Rectangle( //
                gameAreaBounds.x + marginFromSide, //
                bottomLeftY, //
                rectWidth, //
                rectHeight //
        );
        pathRectangles.add(bottomLeftRect); //
        Enemy enemyBL = new Enemy("enemy_lvl3_BL", bottomLeftRect.x, bottomLeftRect.y, new RectangularPath(bottomLeftRect, enemySpeed)); // Cria o inimigo
        enemyBL.setEngine(engine); //
        engine.addEnabled(enemyBL); //

        // 4. Retângulo Inferior Direito (Caminho do Inimigo)
        int bottomRightY = gameAreaBounds.y + gameAreaBounds.height - bottomRectsInitialMargin - rectHeight - bottomRectsExtraPushUp; //
        Rectangle bottomRightRect = new Rectangle( //
                gameAreaBounds.x + gameAreaBounds.width - marginFromSide - rectWidth, //
                bottomRightY, //
                rectWidth, //
                rectHeight //
        );
        pathRectangles.add(bottomRightRect); //
        Enemy enemyBR = new Enemy("enemy_lvl3_BR", bottomRightRect.x, bottomRightRect.y, new RectangularPath(bottomRightRect, enemySpeed)); // Cria o inimigo
        enemyBR.setEngine(engine); //
        engine.addEnabled(enemyBR); //

        // Adicionar os dois novos ObstacleBlocks laterais na parte inferior
        int obstacleWidth = 40; // Largura do obstáculo
        int obstacleHeight = 40; // Altura do obstáculo
        int obstacleY = gameAreaBounds.y + gameAreaBounds.height - 100; // Posição Y dos obstáculos

        // Obstáculo da Esquerda para o Nível 3
        double obstacleLeftX = gameAreaBounds.x + gameAreaBounds.width / 4.0 - obstacleWidth / 2.0; // Posição X do obstáculo esquerdo
        ObstacleBlock leftBlock = new ObstacleBlock("obstacle_L3_left", obstacleLeftX, obstacleY, obstacleWidth, obstacleHeight); // Cria o obstáculo
        leftBlock.setEngine(engine); // Associa o motor ao obstáculo
        engine.addEnabled(leftBlock); // Adiciona o obstáculo ao motor

        // Obstáculo da Direita para o Nível 3
        double obstacleRightX = gameAreaBounds.x + (gameAreaBounds.width * 3.0 / 4.0) - obstacleWidth / 2.0; // Posição X do obstáculo direito
        ObstacleBlock rightBlock = new ObstacleBlock("obstacle_L3_right", obstacleRightX, obstacleY, obstacleWidth, obstacleHeight); // Cria o obstáculo
        rightBlock.setEngine(engine); // Associa o motor ao obstáculo
        engine.addEnabled(rightBlock); // Adiciona o obstáculo ao motor
    }

    /**
     * Devolve a lista de retângulos que definem os caminhos dos inimigos no Nível 3.
     * Estes podem ser usados para desenhar os caminhos no ecrã.
     * @return Uma lista de objetos Rectangle.
     */
    public List<Rectangle> getPathRectanglesForDrawing() {
        return pathRectangles; //
    }

    /**
     * Devolve uma lista vazia, pois o Nível 3 não utiliza linhas azuis decorativas
     * da mesma forma que os níveis anteriores.
     * @return Uma lista vazia de inteiros.
     */
    @Override
    public List<Integer> getBlueLineYPositions() {
        return blueLines; //
    }

    /**
     * Devolve uma lista vazia, pois o Nível 3 não utiliza linhas em ziguezague decorativas.
     * @return Uma lista vazia de inteiros.
     */
    @Override
    public List<Integer> getZigZagLineYPositions() {
        return zigzagLines; //
    }
}