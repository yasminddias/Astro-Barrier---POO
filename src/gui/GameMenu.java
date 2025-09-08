package gui;

import leaderboard.LeaderboardManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Gere a janela principal do jogo e a navegação entre diferentes estados/ecrãs.
 * Responsabilidades incluem exibir o menu inicial, regras, ecrã de jogo, tabela de classificação,
 * ecrã de vitória e ecrã de fim de jogo. Trata os inputs do utilizador para interações de menu.
 * O botão de sair (X) deve funcionar em todos os ecrãs.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv estadoAtual é sempre um MenuState válido.
 * @inv bgPanel nunca é nulo após a conclusão do construtor.
 * @inv leaderboardManager nunca é nulo após a conclusão do construtor.
 * @inv leaderboardPanel nunca é nulo após a conclusão do construtor e é adicionado a bgPanel.
 */
public class GameMenu extends JFrame {
    private enum MenuState { INICIAL, REGRAS, JOGO, LEADERBOARD, VICTORY, GAMEOVER }
    private MenuState estadoAtual = MenuState.INICIAL;

    private Rectangle logicalGameAreaRect;
    private ScaledBackgroundPanel bgPanel;
    public static final int HUD_AREA_HEIGHT = 30;

    private LeaderboardManager leaderboardManager;
    private LeaderboardPanel leaderboardPanel;
    private GameScreen currentGameScreen;

    /**
     * Constrói a janela principal do menu do jogo.
     * Inicializa a UI, o gestor da tabela de classificação, o painel de fundo e configura os listeners de eventos.
     * @post A JFrame é inicializada com o título "Astro Barrier", configurada para fechar ao sair e maximizada.
     * @post LeaderboardManager é instanciado.
     * @post ScaledBackgroundPanel é criado com as imagens de assets e definido como o contentor principal.
     * @post LeaderboardPanel é instanciado, adicionado ao bgPanel e inicialmente definido como invisível.
     * @post Listeners para redimensionamento de componentes, movimento do rato, cliques do rato e pressão de teclas são adicionados.
     * @post A janela é tornada visível e o bgPanel solicita o foco.
     */
    public GameMenu() {
        setTitle("Astro Barrier");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setUndecorated(true);

        leaderboardManager = new LeaderboardManager();

        bgPanel = new ScaledBackgroundPanel(Assets.ARCADE, Assets.GAME_AREA, Assets.LOGO);
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        leaderboardPanel = new LeaderboardPanel(leaderboardManager, bgPanel);
        leaderboardPanel.setVisible(false);
        bgPanel.add(leaderboardPanel);

        MouseAdapter globalExitListener = new MouseAdapter() {
            /**
             * Trata eventos de clique do rato.
             * Verifica PRIMEIRO se o clique foi no botão "Exit" global. Se sim, fecha o jogo.
             * Caso contrário, processa outros cliques com base no estado atual do menu.
             * @param e O evento de rato. Não é nulo.
             * @post Se o clique for no botão "Exit" (X), a aplicação termina.
             * @post Caso contrário, se estadoAtual for INICIAL, trata cliques nos botões "Start" ou "Classificações".
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (bgPanel.getExitButtonBounds().contains(e.getPoint())) {
                    System.exit(0);
                    return;
                }

                if (estadoAtual == MenuState.INICIAL) {
                    if (bgPanel.getStartButtonBounds().contains(e.getPoint())) {
                        showRules();
                    } else if (bgPanel.getLeaderboardButtonBounds().contains(e.getPoint())) {
                        showLeaderboard();
                    }
                }
            }
        };
        bgPanel.addMouseListener(globalExitListener);
        leaderboardPanel.addMouseListener(globalExitListener); // Adicionado para o botão X funcionar no Leaderboard

        addComponentListener(new ComponentAdapter() {
            /**
             * Trata eventos de redimensionamento da janela.
             * Garante que o painel da tabela de classificação é redimensionado corretamente se estiver visível.
             * @param e O evento de componente. Não é nulo.
             * @post Os limites do LeaderboardPanel são atualizados se este estiver visível.
             */
            @Override
            public void componentResized(ComponentEvent e) {
                if (leaderboardPanel.isVisible()) {
                    leaderboardPanel.setBounds(0, 0, bgPanel.getWidth(), bgPanel.getHeight());
                }
                if (currentGameScreen != null && currentGameScreen.isVisible() && bgPanel.getFrameBounds() != null) {
                    Rectangle newLogicalBounds = bgPanel.getFrameBounds();
                    if (logicalGameAreaRect == null || !newLogicalBounds.equals(logicalGameAreaRect)) {
                        logicalGameAreaRect = newLogicalBounds;
                        Rectangle gameScreenVisualBounds = new Rectangle(
                                logicalGameAreaRect.x,
                                logicalGameAreaRect.y,
                                logicalGameAreaRect.width,
                                logicalGameAreaRect.height + HUD_AREA_HEIGHT
                        );
                        currentGameScreen.setBounds(gameScreenVisualBounds);
                    }
                }
                bgPanel.repaint();
            }
            /**
             * Solicita o foco para o painel de fundo quando o componente é exibido.
             * @param e O evento de componente. Não é nulo.
             * @post bgPanel solicita o foco na janela.
             */
            @Override
            public void componentShown(ComponentEvent e) {
                bgPanel.requestFocusInWindow();
            }
        });

        addOtherListeners();
        setVisible(true);
        bgPanel.setFocusable(true);
        bgPanel.requestFocusInWindow();
    }

    /**
     * Adiciona listeners de movimento do rato e de teclado ao painel de fundo (bgPanel).
     * O listener de clique do rato para o botão X já foi adicionado separadamente.
     * @post Listeners de movimento do rato e pressão de teclas são adicionados a bgPanel.
     */
    private void addOtherListeners() {
        bgPanel.addMouseMotionListener(new MouseMotionAdapter() {
            /**
             * Trata eventos de movimento do rato.
             * Usado para efeitos de passagem do rato (hover) nos botões do menu quando no estado INICIAL.
             * @param e O evento de rato. Não é nulo.
             * @post O cursor muda e o estado de hover é atualizado se estiver no estado INICIAL e sobre um botão.
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                bgPanel.handleMouseHover(e.getPoint());
            }
        });


        bgPanel.addKeyListener(new KeyAdapter() {
            /**
             * Trata eventos de pressão de teclas para navegação global no jogo e ações baseadas no estado atual do menu.
             * @param e O evento de tecla. Não é nulo.
             * @post Executa ações como iniciar jogo, retornar ao menu ou limpar tabela de classificação, dependendo do estado atual e da tecla premida.
             */
            @Override
            public void keyPressed(KeyEvent e) {
                switch (estadoAtual) {
                    case INICIAL:
                        break;
                    case REGRAS:
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            startGame();
                        }
                        break;
                    case LEADERBOARD:
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            showInitialMenu();
                        } else if (e.getKeyCode() == KeyEvent.VK_L) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    GameMenu.this,
                                    "Are you sure you want to clear all leaderboard scores?\nThis action cannot be undone.",
                                    "Clear Leaderboard Confirmation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                leaderboardManager.clearScores();
                                if(leaderboardPanel.isVisible()) leaderboardPanel.repaint();
                                JOptionPane.showMessageDialog(
                                        GameMenu.this,
                                        "Leaderboard has been cleared.",
                                        "Leaderboard Cleared",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            }
                        }
                        break;
                    case JOGO:
                        break;
                    case VICTORY:
                        if (e.getKeyCode() == KeyEvent.VK_M) {
                            showInitialMenu();
                        }
                        break;
                    case GAMEOVER:
                        if (e.getKeyCode() == KeyEvent.VK_M) {
                            showInitialMenu();
                        }
                        break;
                }
            }
        });
    }

    /**
     * Limpa ou oculta os componentes específicos do ecrã atual do painel de fundo.
     * Prepara a interface gráfica para a transição para um novo estado de menu ou ecrã de jogo.
     * @post Se currentGameScreen existir e for um filho de bgPanel, é removido e a sua referência é anulada.
     * @post Se leaderboardPanel existir, é tornado invisível.
     * @post As flags showLogo e showGameArea de bgPanel são definidas como false.
     */
    private void clearCurrentScreenView() {
        if (currentGameScreen != null && currentGameScreen.getParent() == bgPanel) {
            bgPanel.remove(currentGameScreen);
            currentGameScreen = null;
        }
        if (leaderboardPanel != null) {
            leaderboardPanel.setVisible(false);
        }
        bgPanel.setShowLogo(false);
        bgPanel.setShowGameArea(false);
    }

    /**
     * Muda a visualização do jogo para o ecrã de vitória.
     * Limpa a vista atual, define o estado para VICTORY, configura a imagem de fundo e redesenha.
     * @post estadoAtual é definido como MenuState.VICTORY.
     * @post A imagem da área de jogo em bgPanel é definida como Assets.VICTORY_SCREEN_IMG.
     * @post bgPanel é configurado para mostrar a área de jogo, é revalidado, redesenhado e solicita o foco.
     */
    public void switchToVictoryScreenState() {
        clearCurrentScreenView();
        this.estadoAtual = MenuState.VICTORY;
        this.bgPanel.setGameAreaImage(Assets.VICTORY_SCREEN_IMG);
        this.bgPanel.setShowGameArea(true);
        this.bgPanel.revalidate();
        this.bgPanel.repaint();
        this.bgPanel.requestFocusInWindow();
    }

    /**
     * Muda a visualização do jogo para o ecrã de fim de jogo (game over).
     * Limpa a vista atual, define o estado para GAMEOVER, configura a imagem de fundo e redesenha.
     * @post estadoAtual é definido como MenuState.GAMEOVER.
     * @post A imagem da área de jogo em bgPanel é definida como Assets.GAMEOVER_SCREEN_IMG.
     * @post bgPanel é configurado para mostrar a área de jogo, é revalidado, redesenhado e solicita o foco.
     */
    public void switchToGameOverScreenState() {
        clearCurrentScreenView();
        this.estadoAtual = MenuState.GAMEOVER;
        this.bgPanel.setGameAreaImage(Assets.GAMEOVER_SCREEN_IMG);
        this.bgPanel.setShowGameArea(true);
        this.bgPanel.revalidate();
        this.bgPanel.repaint();
        this.bgPanel.requestFocusInWindow();
    }

    /**
     * Mostra o menu inicial do jogo.
     * Limpa a vista atual, define o estado para INICIAL, configura as imagens de fundo e logótipo, e redesenha.
     * @post estadoAtual é definido como MenuState.INICIAL.
     * @post A imagem da área de jogo em bgPanel é definida como Assets.GAME_AREA.
     * @post bgPanel é configurado para mostrar o logótipo e a área de jogo, é revalidado, redesenhado e solicita o foco.
     */
    private void showInitialMenu() {
        clearCurrentScreenView();
        estadoAtual = MenuState.INICIAL;
        bgPanel.setGameAreaImage(Assets.GAME_AREA);
        bgPanel.setShowLogo(true);
        bgPanel.setShowGameArea(true);
        bgPanel.revalidate();
        bgPanel.repaint();
        bgPanel.requestFocusInWindow();
    }

    /**
     * Mostra o ecrã de regras do jogo.
     * Limpa a vista atual, define o estado para REGRAS, configura a imagem de fundo das regras e redesenha.
     * @post estadoAtual é definido como MenuState.REGRAS.
     * @post A imagem da área de jogo em bgPanel é definida como Assets.GAME_RULES.
     * @post bgPanel é configurado para mostrar a área de jogo, é revalidado, redesenhado e solicita o foco.
     */
    private void showRules() {
        clearCurrentScreenView();
        estadoAtual = MenuState.REGRAS;
        bgPanel.setGameAreaImage(Assets.GAME_RULES);
        bgPanel.setShowGameArea(true);
        bgPanel.revalidate();
        bgPanel.repaint();
        bgPanel.requestFocusInWindow();
    }

    /**
     * Mostra o ecrã da tabela de classificação.
     * Limpa a vista atual, define o estado para LEADERBOARD, torna o leaderboardPanel visível e o configura, e redesenha.
     * @post estadoAtual é definido como MenuState.LEADERBOARD.
     * @post leaderboardPanel tem os seus limites definidos para preencher bgPanel e é tornado visível.
     * @post bgPanel é revalidado, redesenhado e solicita o foco.
     */
    private void showLeaderboard() {
        clearCurrentScreenView();
        estadoAtual = MenuState.LEADERBOARD;

        leaderboardPanel.setBounds(0, 0, bgPanel.getWidth(), bgPanel.getHeight());
        leaderboardPanel.setVisible(true);

        bgPanel.revalidate();
        bgPanel.repaint();
        bgPanel.requestFocusInWindow();
    }

    /**
     * Inicia o jogo, mudando para o ecrã de jogo (GameScreen).
     * Limpa a vista atual, define o estado para JOGO, configura a imagem de fundo, cria uma nova instância de GameScreen,
     * adiciona-a ao painel de fundo e solicita o foco para o GameScreen.
     * @post estadoAtual é definido como MenuState.JOGO.
     * @post bgPanel é configurado para mostrar a área de jogo padrão.
     * @post Uma nova instância de GameScreen é criada, configurada com os limites apropriados e adicionada a bgPanel.
     * @post O GameScreen recém-criado solicita o foco para receber inputs de jogo.
     */
    private void startGame() {
        clearCurrentScreenView();
        estadoAtual = MenuState.JOGO;
        bgPanel.setGameAreaImage(Assets.GAME_AREA);
        bgPanel.setShowGameArea(true);

        bgPanel.paintImmediately(0,0,bgPanel.getWidth(), bgPanel.getHeight());

        logicalGameAreaRect = bgPanel.getFrameBounds();

        Rectangle gameScreenVisualBounds = new Rectangle(
                logicalGameAreaRect.x,
                logicalGameAreaRect.y,
                logicalGameAreaRect.width,
                logicalGameAreaRect.height + HUD_AREA_HEIGHT
        );

        currentGameScreen = new GameScreen(this, gameScreenVisualBounds, logicalGameAreaRect.height, leaderboardManager);
        currentGameScreen.setOpaque(false);
        currentGameScreen.setBounds(gameScreenVisualBounds);

        bgPanel.add(currentGameScreen);
        bgPanel.setComponentZOrder(currentGameScreen, 0);

        bgPanel.revalidate();
        bgPanel.repaint();
        currentGameScreen.requestFocusInWindow();
    }

    /**
     * O método principal que inicia a aplicação do jogo.
     * Cria uma nova instância de GameMenu na Event Dispatch Thread do Swing.
     * @param args Argumentos da linha de comando (não são utilizados).
     * @post Uma nova instância de GameMenu é criada e agendada para exibição na EDT.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMenu::new);
    }
}