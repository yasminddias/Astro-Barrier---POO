package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Um painel JPanel personalizado que desenha uma imagem de fundo escalonada.
 * Pode também exibir uma imagem de "frame" (moldura) sobreposta e um logótipo animado.
 * Gere estados de rato sobre botões desenhados neste painel e o botão de sair está sempre visível.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv arcadeImg nunca é nulo após a construção.
 * @inv frameBounds, startButtonBounds, exitButtonBounds, e leaderboardButtonBounds nunca são nulos.
 * @inv astroOffset e astroDirection mantêm valores para a animação do logótipo.
 */
public class ScaledBackgroundPanel extends JPanel {
    private final Image arcadeImg;
    private Image frameImg;
    private final Image astroImg;

    private final Rectangle frameBounds = new Rectangle();
    private final Rectangle startButtonBounds = new Rectangle();
    private final Rectangle exitButtonBounds = new Rectangle();
    private final Rectangle leaderboardButtonBounds = new Rectangle();

    private boolean showGameArea = true;
    private boolean showLogo = true;
    private boolean hoverStart = false;
    private boolean hoverExit = false;
    private boolean hoverLeaderboard = false;

    private int astroOffset = 0;
    private int astroDirection = 1;
    private int astroStep = 0;

    /**
     * Constrói um ScaledBackgroundPanel com os caminhos especificados para as imagens.
     * Inicializa as imagens e um temporizador para animar o logótipo.
     * @param arcadePath Caminho para a imagem de fundo principal (arcade). Não deve ser nulo.
     * @param framePath Caminho para a imagem da moldura (área de jogo). Não deve ser nulo.
     * @param astroPath Caminho para a imagem do logótipo (astro). Não deve ser nulo.
     * @post As imagens são carregadas a partir dos caminhos fornecidos.
     * @post Um temporizador é iniciado para animar o logótipo (astroImg).
     * @post O painel é configurado para ser focável, permitindo a receção de eventos de teclado.
     */
    public ScaledBackgroundPanel(String arcadePath, String framePath, String astroPath) {
        this.arcadeImg = new ImageIcon(arcadePath).getImage();
        this.frameImg = new ImageIcon(framePath).getImage();
        this.astroImg = new ImageIcon(astroPath).getImage();

        Timer timer = new Timer(20, e -> {
            astroStep++;
            if (astroStep % 2 == 0) {
                astroOffset += astroDirection;
                if (astroOffset > 8 || astroOffset < -8) {
                    astroDirection *= -1;
                }
            }
            repaint();
        });
        timer.start();

        setFocusable(true);
    }

    /**
     * Gere o estado de passagem do rato sobre os botões desenhados no painel.
     * Atualiza o cursor do rato e solicita o redesenho do painel se o estado de hover mudar.
     * @param p O ponto atual do cursor do rato, em coordenadas relativas ao painel. Não deve ser nulo.
     * @post O estado das flags hoverStart, hoverExit e hoverLeaderboard é atualizado.
     * @post O cursor do rato é alterado para HAND_CURSOR se estiver sobre um dos botões, caso contrário, para o cursor padrão.
     * @post O painel é solicitado a ser redesenhado (repaint) se o estado de hover de algum botão mudou.
     */
    public void handleMouseHover(Point p) {
        boolean hoveringStart = startButtonBounds.contains(p);
        boolean hoveringExit = exitButtonBounds.contains(p);
        boolean hoveringLeaderboard = leaderboardButtonBounds.contains(p);

        boolean needsRepaint = false;
        if (hoveringExit != hoverExit) {
            hoverExit = hoveringExit;
            needsRepaint = true;
        }
        if (showLogo) {
            if (hoveringStart != hoverStart) {
                hoverStart = hoveringStart;
                needsRepaint = true;
            }
            if (hoveringLeaderboard != hoverLeaderboard) {
                hoverLeaderboard = hoveringLeaderboard;
                needsRepaint = true;
            }
        } else {
            if (hoverStart) {
                hoverStart = false;
                needsRepaint = true;
            }
            if (hoverLeaderboard) {
                hoverLeaderboard = false;
                needsRepaint = true;
            }
        }

        if (needsRepaint) {
            repaint();
        }

        boolean isOverAnyActiveButton = hoveringExit || (showLogo && (hoveringStart || hoveringLeaderboard));
        setCursor(isOverAnyActiveButton
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    /**
     * Define a imagem a ser usada para a área de jogo (moldura ou ecrãs de regras/vitória/fim de jogo).
     * @param path O caminho para o novo ficheiro de imagem da área de jogo. Não deve ser nulo.
     * @post A imagem frameImg é atualizada com a imagem carregada do 'path' fornecido e o painel é solicitado a ser redesenhado.
     */
    public void setGameAreaImage(String path) {
        this.frameImg = new ImageIcon(path).getImage();
        repaint();
    }

    /**
     * Define se o logótipo e os elementos do menu inicial (botões, título) devem ser exibidos.
     * @param showLogo Verdadeiro para mostrar o logótipo e elementos do menu, falso para os ocultar.
     * @post O valor da flag showLogo é atualizado.
     */
    public void setShowLogo(boolean showLogo) {
        this.showLogo = showLogo;
    }

    /**
     * Define se a área de jogo (a imagem da moldura, como frameImg) deve ser exibida.
     * @param showGameArea Verdadeiro para mostrar a área de jogo, falso para a ocultar.
     * @post O valor da flag showGameArea é atualizado.
     */
    public void setShowGameArea(boolean showGameArea) {
        this.showGameArea = showGameArea;
    }

    /**
     * Devolve os limites (bounds) da imagem da moldura (frameImg) tal como foi desenhada no último ciclo de pintura.
     * @return Um objeto Rectangle representando os limites da moldura.
     */
    public Rectangle getFrameBounds() {
        return frameBounds;
    }

    /**
     * Devolve os limites do botão "START" tal como foram definidos no último ciclo de pintura.
     * @return Um objeto Rectangle representando os limites do botão "START".
     */
    public Rectangle getStartButtonBounds() {
        return startButtonBounds;
    }

    /**
     * Devolve os limites do botão "X" (Sair) tal como foram definidos no último ciclo de pintura.
     * @return Um objeto Rectangle representando os limites do botão "X".
     */
    public Rectangle getExitButtonBounds() {
        return exitButtonBounds;
    }

    /**
     * Devolve os limites do botão "CLASSIFICAÇÕES" tal como foram definidos no último ciclo de pintura.
     * @return Um objeto Rectangle representando os limites do botão "CLASSIFICAÇÕES".
     */
    public Rectangle getLeaderboardButtonBounds() {
        return leaderboardButtonBounds;
    }

    /**
     * Desenha os componentes do painel, incluindo imagens de fundo, logótipo, título e botões interativos.
     * As imagens são escalonadas. Os elementos do menu (título, logótipo, botões START e CLASSIFICAÇÕES) são desenhados se showLogo for verdadeiro.
     * O botão "X" (Sair) é sempre desenhado.
     * @param g O contexto gráfico usado para desenhar. Não deve ser nulo.
     * @post O painel é desenhado com todos os seus elementos visuais. Os limites dos botões e da moldura são atualizados.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth(), h = getHeight();
        int aw = arcadeImg.getWidth(this), ah = arcadeImg.getHeight(this);
        double aScale = Math.min((double) w / aw, (double) h / ah);
        int scaledAW = (int) (aw * aScale);
        int scaledAH = (int) (ah * aScale);
        int ax = (w - scaledAW) / 2;
        int ay = (h - scaledAH) / 2;
        g2.drawImage(arcadeImg, ax, ay, scaledAW, scaledAH, this);

        int fx = 0, fy = 0, scaledFW = 0, scaledFH = 0;

        if (frameImg != null) {
            int fw = frameImg.getWidth(this), fh = frameImg.getHeight(this);
            scaledFW = (int) (fw * 0.6 * aScale);
            scaledFH = (int) (fh * 0.6 * aScale);
            fx = (w - scaledFW) / 2;
            fy = (h - scaledFH) / 2;

            if (showGameArea) {
                g2.drawImage(frameImg, fx, fy, scaledFW, scaledFH, this);
                frameBounds.setBounds(fx, fy, scaledFW, scaledFH);
            }
        }

        int xBtnSize = 40;
        int xBtnX = w - xBtnSize - 20;
        int xBtnY = 20;
        exitButtonBounds.setBounds(xBtnX, xBtnY, xBtnSize, xBtnSize);

        g2.setColor(hoverExit ? new Color(80, 80, 80) : new Color(60, 60, 60));
        g2.fillRect(xBtnX, xBtnY, xBtnSize, xBtnSize);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("X", xBtnX + 13, xBtnY + 27);


        if (frameImg != null && showLogo) {
            if (astroImg != null) {
                int astroW = astroImg.getWidth(this), astroH = astroImg.getHeight(this);
                double astroScale = 0.9;
                int scaledAstroW = (int) (scaledFW * astroScale);
                int scaledAstroH = (int) ((double) astroH / astroW * scaledAstroW);
                int ax2 = fx + (scaledFW - scaledAstroW) / 2;
                int logoYOffset = scaledFH / 8;
                int ay2 = fy + (scaledFH - scaledAstroH) / 2 - logoYOffset + astroOffset;
                g2.drawImage(astroImg, ax2, ay2, scaledAstroW, scaledAstroH, this);
            }

            String title = "Astro Barrier";
            g2.setFont(new Font("Arial", Font.BOLD, (int) (20 * aScale)));
            g2.setColor(Color.WHITE);

            FontMetrics titlefm = g2.getFontMetrics();
            int textWidth = titlefm.stringWidth(title);
            int textX = fx + (scaledFW - textWidth) / 2;
            int titleY = fy - 20;
            if (!showGameArea) {
                titleY = ay + scaledAH / 4;
                textX = (w - textWidth) / 2;
            }
            g2.drawString(title, textX, titleY);

            int btnW = (int) (scaledFW / 2.5);
            int btnH = (int) (scaledFH / 12);
            int btnSpacing = btnH / 2;

            int startBtnY = fy + scaledFH - btnH - 80;

            int startBtnX = fx + (scaledFW - btnW) / 2;
            startButtonBounds.setBounds(startBtnX, startBtnY, btnW, btnH);

            g2.setColor(hoverStart ? new Color(255, 100, 100) : Color.RED);
            g2.fillRoundRect(startBtnX, startBtnY, btnW, btnH, 15, 15);
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, btnH / 2));
            FontMetrics fm = g2.getFontMetrics();
            String startText = "START";
            int startTextW = fm.stringWidth(startText);
            int startTextH = fm.getAscent();
            g2.drawString(startText, startBtnX + (btnW - startTextW) / 2, startBtnY + (btnH + startTextH) / 2 - fm.getDescent()/2);

            int leaderboardBtnX = startBtnX;
            int leaderboardBtnY = startBtnY + btnH + btnSpacing;
            leaderboardButtonBounds.setBounds(leaderboardBtnX, leaderboardBtnY, btnW, btnH);

            g2.setColor(hoverLeaderboard ? new Color(100, 100, 255) : Color.BLUE);
            g2.fillRoundRect(leaderboardBtnX, leaderboardBtnY, btnW, btnH, 15, 15);
            g2.setColor(Color.WHITE);

            String leaderboardText = "LEADERBOARD";
            g2.setFont(new Font("Arial", Font.BOLD, btnH / 2));
            fm = g2.getFontMetrics();
            int leaderboardTextW = fm.stringWidth(leaderboardText);
            int leaderboardTextH = fm.getAscent();

            if (leaderboardTextW > btnW - 10) {
                g2.setFont(new Font("Arial", Font.BOLD, (int)(btnH / 2.8)));
                fm = g2.getFontMetrics();
                leaderboardTextW = fm.stringWidth(leaderboardText);
                leaderboardTextH = fm.getAscent();
            }
            g2.drawString(leaderboardText, leaderboardBtnX + (btnW - leaderboardTextW) / 2, leaderboardBtnY + (btnH + leaderboardTextH) / 2 - fm.getDescent()/2);
        }
    }
}