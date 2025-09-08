package gui;

import engine.GameEngine;
import gameobject.behaviour.PlayerBehaviour;
import leaderboard.LeaderboardManager;
import gamelevel.Level;
import gamelevel.Level1;
import gamelevel.Level2;
import gamelevel.Level3;
import gameobject.IGameObject;
import gameobject.behaviour.IBehaviour;
import gameobject.entity.PlayerShip;
import gameobject.geometry.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa a área principal de jogo onde o jogador interage com os objetos do jogo.
 * Gere o estado do jogo, incluindo níveis, vidas do jogador, pontuação e a renderização de todas as entidades.
 * Interage com o GameEngine para executar a lógica do jogo e atualizar os estados dos objetos.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv engine nunca é nulo.
 * @inv keysPressed nunca é nulo.
 * @inv A lista levels nunca é nula e contém objetos Level válidos.
 * @inv player nunca é nulo após a chamada a initPlayer.
 * @inv leaderboardManager nunca é nulo.
 * @inv mainGameMenu nunca é nulo.
 * @inv playerLives é sempre não negativo.
 * @inv currentScore é sempre não negativo.
 */
public class GameScreen extends JPanel {
    private final GameEngine engine;
    private final Set<Integer> keysPressed = new HashSet<>();
    private final List<Level> levels = new ArrayList<>();
    private int currentLevelIndex = 0;

    private PlayerShip player;
    private Point[] bulletPositionsUI;
    private final Image bulletIconImage;
    private final Image blueLineImage;
    private final Image zigzagLineImage;
    private final Image level3LineImage;

    private final List<Integer> lineYPositions = new ArrayList<>();
    private final List<Integer> zigzagLineYPositions = new ArrayList<>();
    private final List<Rectangle> pathRectanglesToDrawLvl3 = new ArrayList<>();

    private boolean levelFinished = false;

    private int playerLives;
    private static final int INITIAL_PLAYER_LIVES = 3;
    private boolean gameOver = false;

    private final int logicalGameHeight;

    private int currentScore;
    private int scoreAtStartOfThisAttempt;
    private static final int POINTS_PER_ENEMY = 10;
    private static final int POINTS_PER_REMAINING_BULLET = 10;
    private LeaderboardManager leaderboardManager;
    private GameMenu mainGameMenu;

    /**
     * Constrói o painel GameScreen.
     * Inicializa o motor de jogo, jogador, níveis, elementos da UI e inicia o fluxo do jogo.
     * @param frame A janela principal GameMenu, usada para contexto e mudança de ecrãs. Não deve ser nula.
     * @param visualBoundsInParent Os limites deste painel dentro do seu contentor pai. Não deve ser nulo.
     * @param logicalGameHeightParam A altura lógica da área de jogo, excluindo o HUD. Deve ser positiva.
     * @param lm A instância de LeaderboardManager para gestão de pontuações. Não deve ser nula.
     * @post GameScreen é inicializado, focável e opaco.
     * @post GameEngine, jogador, níveis e elementos do HUD são inicializados.
     * @post Listeners de teclado e o temporizador do ciclo de jogo são iniciados.
     * @post O primeiro nível é carregado.
     */
    public GameScreen(GameMenu frame, Rectangle visualBoundsInParent, int logicalGameHeightParam, LeaderboardManager lm) {
        this.mainGameMenu = frame;
        this.logicalGameHeight = logicalGameHeightParam;
        this.playerLives = INITIAL_PLAYER_LIVES;
        this.currentScore = 0;
        this.scoreAtStartOfThisAttempt = 0;
        this.leaderboardManager = lm;

        setBounds(visualBoundsInParent);
        setOpaque(false);
        setFocusable(true);
        requestFocusInWindow();

        engine = new GameEngine();
        Rectangle logicalBoundsEngine = new Rectangle(25, 10, 365, 400);
        engine.setBounds(logicalBoundsEngine);

        bulletIconImage = new ImageIcon(Assets.BULLET).getImage();
        bulletPositionsUI = initBulletUI(visualBoundsInParent.height);
        blueLineImage = new ImageIcon(Assets.BLUE_LINE).getImage();
        zigzagLineImage = new ImageIcon(Assets.ZIGZAG_LINE).getImage();
        level3LineImage = new ImageIcon(Assets.LEVEL3_LINE).getImage();

        initPlayer(logicalBoundsEngine);

        levels.add(new Level1());
        levels.add(new Level2());
        levels.add(new Level3());

        startGameFlow();


        addKeyListener(new KeyAdapter() {
            /**
             * Trata os eventos de teclas premidas durante o jogo.
             * Adiciona as teclas premidas a um conjunto para ações contínuas e trata ações discretas
             * como reiniciar o nível.
             * @param e O evento de tecla. Não deve ser nulo.
             * @post O keyCode da tecla premida é adicionado a keysPressed.
             * @post Se 'R' for premido e o jogo não tiver terminado, a tentativa atual do nível é reiniciada.
             */
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    if (!gameOver) {
                        resetLevel(true);
                    }
                }
            }

            /**
             * Trata os eventos de teclas libertadas durante o jogo.
             * Remove as teclas libertadas do conjunto de teclas premidas.
             * @param e O evento de tecla. Não deve ser nulo.
             * @post O keyCode da tecla libertada é removido de keysPressed.
             */
            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });

        new Timer(16, (ActionEvent e) -> {
            if (gameOver) {
                return;
            }

            engine.run(0.016, keysPressed::contains);

            for (IGameObject go : engine.getEnabled()) {
                IBehaviour behaviour = go.behaviour();
                if (behaviour != null && go.name().startsWith("enemy")) {
                    if (behaviour.wasJustFrozenAndClearFlag()) {
                        addScore(POINTS_PER_ENEMY);
                    }
                }
            }

            repaint();

            if (levelFinished) {
                return;
            }

            IBehaviour playerBehaviourInterface = null;
            if (player != null && player.behaviour() != null) {
                playerBehaviourInterface = player.behaviour();
            }

            List<IGameObject> allGameObjects = engine.getEnabled();
            boolean hasUnfrozenEnemies = false;
            int activeEnemyCount = 0;
            for (IGameObject go : allGameObjects) {
                if (go.name().startsWith("enemy")) {
                    activeEnemyCount++;
                    IBehaviour enemyBhv = go.behaviour();
                    if (enemyBhv != null && !enemyBhv.isCurrentlyFrozen()) {
                        hasUnfrozenEnemies = true;
                    }
                }
            }

            boolean bulletsStillOnScreen = false;
            for (IGameObject go : allGameObjects) {
                if (go.name().startsWith("player_bullet")) {
                    bulletsStillOnScreen = true;
                    break;
                }
            }

            if (playerBehaviourInterface != null &&
                    playerBehaviourInterface.getDisplayBulletCount() <= 0 &&
                    !bulletsStillOnScreen &&
                    hasUnfrozenEnemies) {
                playerLives--;
                if (playerLives <= 0) {
                    playerLives = 0;
                    showGameOverScreen();
                } else {
                    resetLevel(true);
                }
                return;
            }

            if (!levelFinished) {
                if (activeEnemyCount > 0 && !hasUnfrozenEnemies) {
                    levelFinished = true;
                    addPointsForRemainingBullets(playerBehaviourInterface);
                    nextLevel();
                } else if (activeEnemyCount == 0 && !bulletsStillOnScreen) {
                    levelFinished = true;
                    addPointsForRemainingBullets(playerBehaviourInterface);
                    nextLevel();
                }
            }
        }).start();
    }

    /**
     * Inicia ou reinicia o fluxo principal do jogo.
     * Redefine a pontuação, o índice do nível atual, as vidas do jogador e os estados de fim de jogo/nível.
     * Carrega o primeiro nível.
     * @post A pontuação e scoreAtStartOfThisAttempt são definidos como 0.
     * @post currentLevelIndex é definido como 0.
     * @post playerLives é redefinido para INITIAL_PLAYER_LIVES.
     * @post gameOver e levelFinished são definidos como false.
     * @post O nível atual (primeiro nível) é carregado.
     */
    private void startGameFlow() {
        this.currentScore = 0;
        this.scoreAtStartOfThisAttempt = 0;
        this.currentLevelIndex = 0;
        this.playerLives = INITIAL_PLAYER_LIVES;
        this.gameOver = false;
        this.levelFinished = false;
        loadCurrentLevel();
    }

    /**
     * Reinicia o estado do nível atual.
     * Remove todos os inimigos, projéteis do jogador e obstáculos. Recarrega os objetos do nível.
     * Opcionalmente, reverte a pontuação para o valor que tinha no início da tentativa atual do nível.
     * @param revertScoreToStartOfAttempt Se verdadeiro, a pontuação atual é revertida para a pontuação no início desta tentativa de nível.
     * @post Todos os inimigos, projéteis do jogador e obstáculos são removidos do motor de jogo.
     * @post O nível atual é recarregado.
     * @post O estado do jogador (ex: contagem de projéteis) é reiniciado.
     * @post levelFinished é definido como false.
     * @post Se revertScoreToStartOfAttempt for verdadeiro, currentScore é atualizado.
     */
    private void resetLevel(boolean revertScoreToStartOfAttempt) {
        if (gameOver) return;

        if (revertScoreToStartOfAttempt) {
            this.currentScore = this.scoreAtStartOfThisAttempt;
        }

        List<IGameObject> toRemove = new ArrayList<>();
        for (IGameObject go : engine.getEnabled()) {
            if (go.name().startsWith("enemy") ||
                    go.name().startsWith("player_bullet") ||
                    go.name().startsWith("obstacle")) {
                toRemove.add(go);
            }
        }
        for (IGameObject go : toRemove) {
            engine.destroy(go);
        }

        loadCurrentLevel();

        if (player != null && player.behaviour() != null) {
            player.behaviour().resetPlayerSpecificState();
        }
        levelFinished = false;
    }

    /**
     * Adiciona pontos à pontuação atual do jogador.
     * @param points O número de pontos a adicionar. Pressupõe-se que seja não negativo.
     * @post currentScore é incrementado pelo valor de points.
     */
    public void addScore(int points) {
        this.currentScore += points;
    }

    /**
     * Adiciona pontos à pontuação do jogador com base nos projéteis restantes.
     * @param pb O comportamento do jogador (IBehaviour) para obter a contagem de projéteis. Pode ser nulo.
     * @post Se pb não for nulo, pontos são adicionados à pontuação atual com base nos projéteis restantes e POINTS_PER_REMAINING_BULLET.
     */
    private void addPointsForRemainingBullets(IBehaviour pb) {
        if (pb != null) {
            int bulletsLeft = pb.getDisplayBulletCount();
            if (bulletsLeft >= 0) {
                addScore(bulletsLeft * POINTS_PER_REMAINING_BULLET);
            }
        }
    }

    /**
     * Inicializa o objeto do jogador (PlayerShip).
     * Configura a sua posição inicial, associa-o ao motor de jogo e adiciona-o aos objetos ativos.
     * @param logicalBoundsEngine Os limites lógicos do motor de jogo, usados para posicionar o jogador. Não deve ser nulo.
     * @post O objeto player é criado e configurado.
     * @post O comportamento do jogador é vinculado ao motor de jogo.
     * @post O jogador é adicionado à lista de objetos ativos do motor de jogo.
     */
    private void initPlayer(Rectangle logicalBoundsEngine) {
        player = new PlayerShip("player",
                logicalBoundsEngine.getMinX() + logicalBoundsEngine.getWidth() / 2.0,
                logicalBoundsEngine.getMinY() + logicalBoundsEngine.getHeight() - 20);

        IBehaviour playerBhv = player.behaviour();
        if (playerBhv != null) {
            playerBhv.linkGameEngine(engine);
        }
        engine.addEnabled(player);
    }

    /**
     * Carrega os objetos e configurações para o nível atual.
     * Regista a pontuação no início da tentativa do nível, limpa decorações de níveis anteriores
     * e carrega os elementos específicos do nível (inimigos, obstáculos, linhas decorativas).
     * @post Se currentLevelIndex for válido, scoreAtStartOfThisAttempt é atualizado com currentScore.
     * @post levelFinished é definido como false.
     * @post Os objetos do nível atual são carregados no motor de jogo.
     * @post As listas de posições de linhas decorativas (lineYPositions, zigzagLineYPositions, pathRectanglesToDrawLvl3) são limpas e preenchidas conforme o nível atual.
     */
    private void loadCurrentLevel() {
        if (currentLevelIndex < levels.size()) {
            this.scoreAtStartOfThisAttempt = this.currentScore;

            levelFinished = false;
            Level current = levels.get(currentLevelIndex);
            current.load(engine);

            lineYPositions.clear();
            zigzagLineYPositions.clear();
            pathRectanglesToDrawLvl3.clear();

            List<Integer> blueLines = current.getBlueLineYPositions();
            if (blueLines != null) lineYPositions.addAll(blueLines);

            List<Integer> zigzagLines = current.getZigZagLineYPositions();
            if (zigzagLines != null) zigzagLineYPositions.addAll(zigzagLines);

            List<Rectangle> rectPaths = current.getPathRectanglesForDrawing();
            if (rectPaths != null) {
                pathRectanglesToDrawLvl3.addAll(rectPaths);
            }
        }
    }

    /**
     * Avança para o próximo nível do jogo.
     * Se houver mais níveis, reinicia o estado para o novo nível.
     * Se todos os níveis foram concluídos, mostra o ecrã de vitória.
     * @post currentLevelIndex é incrementado.
     * @post Se houver um próximo nível, o método resetLevel é chamado para prepará-lo.
     * @post Se todos os níveis foram concluídos, o método showVictoryScreen é chamado.
     */
    private void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            resetLevel(false);
        } else {
            showVictoryScreen();
        }
    }

    /**
     * Solicita ao jogador as suas iniciais e guarda a pontuação na tabela de classificação.
     * Apresenta um diálogo para inserção de iniciais.
     * @post Um JOptionPane é exibido para o jogador inserir as iniciais.
     * @post A pontuação atual é adicionada à tabela de classificação com as iniciais fornecidas (ou "???" se cancelado/vazio).
     */
    private void promptForInitialsAndSaveScore() {
        String initials = JOptionPane.showInputDialog(
                mainGameMenu,
                "You Won! Enter your initials (max 3 characters):",
                "New High Score!",
                JOptionPane.PLAIN_MESSAGE
        );
        if (initials == null) {
            initials = "???";
        }
        leaderboardManager.addScore(initials, currentScore);
    }

    /**
     * Transita o jogo para o estado de vitória.
     * Define gameOver como verdadeiro, solicita iniciais para a pontuação e muda para o ecrã de vitória.
     * @post gameOver é definido como true.
     * @post As iniciais do jogador são solicitadas e a pontuação é guardada.
     * @post O GameMenu é instruído a mudar para o estado de ecrã de vitória.
     */
    private void showVictoryScreen() {
        gameOver = true;
        promptForInitialsAndSaveScore();
        mainGameMenu.switchToVictoryScreenState();
    }

    /**
     * Transita o jogo para o estado de fim de jogo (game over).
     * Define gameOver como verdadeiro e muda para o ecrã de fim de jogo.
     * @post gameOver é definido como true.
     * @post O GameMenu é instruído a mudar para o estado de ecrã de fim de jogo.
     */
    private void showGameOverScreen() {
        gameOver = true;
        mainGameMenu.switchToGameOverScreenState();
    }

    /**
     * Inicializa as posições dos ícones de projéteis na interface do utilizador (HUD).
     * @param gameScreenPanelHeight A altura do painel GameScreen, usada para calcular a posição Y do HUD.
     * @return Um array de objetos Point representando as posições centrais dos ícones de projéteis no HUD.
     */
    private Point[] initBulletUI(int gameScreenPanelHeight) {
        Point[] positions = new Point[PlayerBehaviour.MAX_BULLETS];
        int startX = 15;
        int hudY = gameScreenPanelHeight - (GameMenu.HUD_AREA_HEIGHT / 2);

        int spacing = 20;
        if (bulletIconImage != null && bulletIconImage.getWidth(null) > 0) {
            spacing = bulletIconImage.getWidth(null) + 5;
        }
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Point(startX + i * spacing, hudY);
        }
        return positions;
    }

    /**
     * Desenha um único objeto de jogo (IGameObject) no ecrã.
     * Obtém a transformação do objeto e invoca o método render da sua forma (shape).
     * @param g2 O contexto gráfico 2D usado для desenho. Não deve ser nulo.
     * @param go O objeto de jogo a ser desenhado. Não deve ser nulo.
     * @post O objeto 'go' é renderizado no contexto gráfico 'g2' de acordo com a sua forma e transformação.
     */
    private void drawGameObject(Graphics2D g2, IGameObject go) {
        var t = go.transform();
        go.shape().render(g2,
                (int) t.position().getX(),
                (int) t.position().getY(),
                t.angle(),
                t.scale(),
                t.layer());
    }

    /**
     * Desenha todos os componentes visuais do ecrã de jogo.
     * Inclui linhas decorativas, todos os objetos de jogo ativos (jogador, inimigos, projéteis, obstáculos)
     * e os elementos do HUD (ícones de projéteis, vidas, pontuação, indicador de nível).
     * Este método é chamado pelo sistema Swing sempre que o painel precisa ser redesenhado.
     * @param g O contexto gráfico usado para desenhar. Não deve ser nulo.
     * @post O ecrã de jogo é completamente desenhado no contexto gráfico fornecido.
     * Se gameOver for verdadeiro, o método retorna sem desenhar os elementos do jogo.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameOver) {
            return;
        }

        if (blueLineImage != null && lineYPositions != null && !lineYPositions.isEmpty()) {
            int margin = 8;
            int desiredWidth = getWidth() - 2 * margin;
            int desiredHeight = blueLineImage.getHeight(null);
            for (int yPos : lineYPositions) {
                g2.drawImage(blueLineImage, margin, yPos - desiredHeight / 2, desiredWidth, desiredHeight, null);
            }
        }
        if (zigzagLineImage != null && zigzagLineYPositions != null && !zigzagLineYPositions.isEmpty()) {
            int margin = 8;
            int desiredWidth = getWidth() - 2 * margin;
            int desiredHeight = zigzagLineImage.getHeight(null);
            for (int yPos : zigzagLineYPositions) {
                g2.drawImage(zigzagLineImage, margin, yPos - desiredHeight / 2, desiredWidth, desiredHeight, null);
            }
        }

        if (level3LineImage != null && level3LineImage.getWidth(null) > 0 &&
                pathRectanglesToDrawLvl3 != null && !pathRectanglesToDrawLvl3.isEmpty()) {

            for (Rectangle rectPath : pathRectanglesToDrawLvl3) {
                g2.drawImage(level3LineImage, rectPath.x, rectPath.y, rectPath.width, rectPath.height, null);
            }
        }


        for (IGameObject go : engine.getEnabled()) {
            drawGameObject(g2, go);
        }

        Font hudFont = new Font("Arial", Font.BOLD, 16);
        g2.setFont(hudFont);
        FontMetrics fm = g2.getFontMetrics();
        int textHeight = fm.getHeight();
        int textAscent = fm.getAscent();

        int bottomHudCenterY = 0;
        if (bulletPositionsUI != null && bulletPositionsUI.length > 0 && bulletPositionsUI[0] != null) {
            bottomHudCenterY = (int) bulletPositionsUI[0].getY();
        } else {
            bottomHudCenterY = getHeight() - (GameMenu.HUD_AREA_HEIGHT / 2);
        }
        int bottomHudTextBaselineY = bottomHudCenterY - (textHeight / 2) + textAscent;

        if (bulletPositionsUI != null && bulletPositionsUI.length > 0 && bulletPositionsUI[0] != null &&
                player != null && bulletIconImage != null && bulletIconImage.getHeight(null) > 0) {
            IBehaviour playerBhv = player.behaviour();
            if (playerBhv != null) {
                int currentBullets = playerBhv.getDisplayBulletCount();
                int maxBullets = playerBhv.getMaxDisplayBullets();
                if (maxBullets <= 0 && PlayerBehaviour.MAX_BULLETS > 0) maxBullets = PlayerBehaviour.MAX_BULLETS;


                for (int i = 0; i < maxBullets; i++) {
                    if (i < currentBullets) {
                        int x = (int) bulletPositionsUI[i].getX();
                        int y = (int) bulletPositionsUI[i].getY();
                        g2.drawImage(bulletIconImage,
                                x - bulletIconImage.getWidth(null) / 2,
                                y - bulletIconImage.getHeight(null) / 2,
                                null);
                    }
                }
            }
        }

        g2.setColor(Color.YELLOW);
        String livesText = "Lives: " + playerLives;
        int livesTextWidth = fm.stringWidth(livesText);
        int livesTextX = (getWidth() - livesTextWidth) / 2;
        g2.drawString(livesText, livesTextX, bottomHudTextBaselineY);

        g2.setColor(Color.YELLOW);
        String scoreTextValue = "" + currentScore;
        int scoreTextWidth = fm.stringWidth(scoreTextValue);
        int scoreTextX = getWidth() - scoreTextWidth - 15;
        g2.drawString(scoreTextValue, scoreTextX, bottomHudTextBaselineY);

        g2.setColor(Color.WHITE);
        String levelStr = "L" + (currentLevelIndex + 1);
        int levelTextWidth = fm.stringWidth(levelStr);
        int levelTextX = getWidth() - levelTextWidth - 15;
        int levelTextY = fm.getAscent() + 10;
        g2.drawString(levelStr, levelTextX, levelTextY);
    }
}