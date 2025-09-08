package gui;

import leaderboard.LeaderboardManager;
import leaderboard.ScoreEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Um painel JPanel para exibir a tabela de classificação (leaderboard).
 * Obtém as pontuações do LeaderboardManager e formata-as para exibição.
 * Inclui indicações para interações do utilizador (sair, limpar pontuações).
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv leaderboardManager nunca é nulo.
 * @inv bgPanelRef pode ser nulo; se não for nulo, é uma referência ao painel de fundo para possíveis interações.
 */
public class LeaderboardPanel extends JPanel {
    private LeaderboardManager leaderboardManager; // Gestor da tabela de classificação
    private ScaledBackgroundPanel bgPanelRef; // Referência ao painel de fundo, pode ser usado para repintar o pai se necessário

    /**
     * Constrói um LeaderboardPanel.
     * @param lm O LeaderboardManager do qual as pontuações serão obtidas. Não deve ser nulo.
     * @param bgPanel Uma referência ao ScaledBackgroundPanel, pode ser usado para interações (ex: solicitar redesenho). Pode ser nulo.
     * @post O painel é configurado para ser transparente (setOpaque(false)) para que o ScaledBackgroundPanel subjacente possa ser visível, ou pode ter um fundo específico se a opacidade for alterada.
     * @post As referências a LeaderboardManager e ScaledBackgroundPanel são armazenadas.
     */
    public LeaderboardPanel(LeaderboardManager lm, ScaledBackgroundPanel bgPanel) {
        this.leaderboardManager = lm; // Armazena a referência ao gestor da tabela
        this.bgPanelRef = bgPanel; // Armazena a referência ao painel de fundo
        setOpaque(false); // Torna este painel transparente para que o painel de fundo seja visível
        // Em alternativa, definir um fundo específico:
        // setBackground(new Color(10, 10, 30)); // Azul escuro
    }

    /**
     * Desenha o conteúdo do painel da tabela de classificação.
     * Inclui um fundo semi-transparente, o título "TOP 10 SCORES", as entradas da tabela de classificação
     * (ou uma mensagem se estiver vazia), uma mensagem para retornar ao menu principal (ESC)
     * e uma mensagem para limpar as pontuações (L).
     * @param g O contexto gráfico usado para desenhar. Não deve ser nulo.
     * @post O painel é desenhado com o título, as pontuações (ou mensagem de ausência de pontuações), a mensagem de saída (ESC) e a mensagem de limpar pontuações (L). O anti-aliasing é ativado para texto mais suave.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Chama o método da superclasse
        Graphics2D g2 = (Graphics2D) g; // Converte para Graphics2D

        // Desenha um fundo distinto para o ecrã da tabela de classificação
        g2.setColor(new Color(20, 20, 50, 230)); // Azul escuro semi-transparente
        g2.fillRect(0, 0, getWidth(), getHeight()); // Preenche todo o painel

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Ativa o anti-aliasing para texto mais suave

        // Desenha o título
        g2.setColor(Color.CYAN); // Cor do título
        g2.setFont(new Font("Monospaced", Font.BOLD, 36)); // Fonte do título
        FontMetrics fmTitle = g2.getFontMetrics(); // Obtém métricas da fonte para centralizar o título
        String title = "TOP 10 SCORES"; //
        int titleWidth = fmTitle.stringWidth(title); // Largura do título
        g2.drawString(title, (getWidth() - titleWidth) / 2, 80); // Desenha o título centralizado no topo

        // Prepara para desenhar as entradas da tabela de classificação
        g2.setColor(Color.WHITE); // Cor do texto das entradas
        g2.setFont(new Font("Monospaced", Font.PLAIN, 22)); // Fonte das entradas
        FontMetrics fmEntry = g2.getFontMetrics(); // Métricas da fonte das entradas

        List<ScoreEntry> scores = leaderboardManager.getScores(); // Obtém as pontuações
        int yPos = 150; // Posição Y inicial para a primeira entrada
        int rank = 1; // Ranking inicial
        for (ScoreEntry entry : scores) { // Itera sobre as pontuações
            if (rank > 10) break; // Mostra apenas o top 10
            String rankStr = String.format("%2d.", rank++); // Formata o ranking
            // Formata a linha da pontuação para alinhamento consistente
            String scoreLine = String.format("%s %-3S ..... %6d", rankStr, entry.getInitials(), entry.getScore()); //
            g2.drawString(scoreLine, getWidth() / 2 - fmEntry.stringWidth(scoreLine) / 2, yPos); // Desenha a entrada centralizada
            yPos += fmEntry.getHeight() + 8; // Incrementa a posição Y para a próxima entrada, com espaçamento
        }

        if (scores.isEmpty()) { // Se não houver pontuações
            String noScoresMsg = "No scores yet. Be the first!"; // Mensagem a exibir
            int msgWidth = fmEntry.stringWidth(noScoresMsg); // Largura da mensagem
            g2.drawString(noScoresMsg, (getWidth() - msgWidth) / 2, yPos); // Desenha a mensagem centralizada
            yPos += fmEntry.getHeight() + 8; // Atualiza yPos caso haja mais texto abaixo
        }

        // Desenha a mensagem de instrução para sair
        g2.setColor(Color.YELLOW); // Cor da mensagem de saída
        g2.setFont(new Font("Arial", Font.PLAIN, 18)); // Fonte da mensagem de saída
        String exitMsg = "Press ESC to return to Main Menu"; //
        FontMetrics exitFm = g2.getFontMetrics(); // Métricas da fonte da mensagem de saída
        int exitMsgWidth = exitFm.stringWidth(exitMsg); // Largura da mensagem de saída
        int exitMsgY = getHeight() - 50; // Posição Y da mensagem de saída, perto do fundo
        g2.drawString(exitMsg, (getWidth() - exitMsgWidth) / 2, exitMsgY); // Desenha a mensagem de saída centralizada

        // Adiciona a indicação para a tecla 'L' para limpar pontuações
        String clearHintMsg = "Press L to Clear Scores";
        // Usa a mesma fonte e cor que a mensagem de saída (ESC)
        int clearHintWidth = exitFm.stringWidth(clearHintMsg);
        // Posiciona acima da mensagem de ESC
        int clearHintY = exitMsgY - exitFm.getHeight() - 5; // Alguns pixels acima
        g2.drawString(clearHintMsg, (getWidth() - clearHintWidth) / 2, clearHintY); // Desenha a indicação
    }
}