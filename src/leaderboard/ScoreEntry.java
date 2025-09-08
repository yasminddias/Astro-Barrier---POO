package leaderboard;

import java.io.Serializable;

/**
 * Representa uma entrada individual na tabela de classificação (leaderboard).
 * Contém as iniciais do jogador e a respetiva pontuação.
 * Esta classe é serializável para permitir a persistência dos dados e
 * é comparável para facilitar a ordenação das pontuações.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv As iniciais (initials) nunca são nulas, têm um comprimento máximo de 3 caracteres e estão em maiúsculas. Se vazias ou nulas na origem, assumem o valor "???".
 * @inv A pontuação (score) é um inteiro não negativo.
 */
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {
    private static final long serialVersionUID = 1L;
    private String initials;
    private int score;

    /**
     * Constrói uma ScoreEntry com as iniciais e pontuação especificadas.
     * As iniciais são sanitizadas (espaços removidos, convertidas para maiúsculas, limitadas em comprimento e,
     * se vazias/nulas, definidas como "???").
     * @param initials As iniciais do jogador. Serão sanitizadas para um máximo de 3 caracteres, em maiúsculas. Assumem "???" se nulas ou vazias.
     * @param score A pontuação do jogador. Pressupõe-se que seja um valor não negativo.
     * @post Uma nova ScoreEntry é criada com as iniciais sanitizadas e a pontuação fornecida.
     */
    public ScoreEntry(String initials, int score) {
        this.initials = initials != null ? initials.trim().toUpperCase() : "???";
        if (this.initials.length() > 3) {
            this.initials = this.initials.substring(0, 3);
        } else if (this.initials.isEmpty()) {
            this.initials = "???";
        }
        this.score = score;
    }

    /**
     * Devolve as iniciais do jogador.
     * @return As iniciais do jogador (máximo de 3 caracteres, em maiúsculas).
     */
    public String getInitials() {
        return initials;
    }

    /**
     * Devolve a pontuação do jogador.
     * @return A pontuação do jogador.
     */
    public int getScore() {
        return score;
    }

    /**
     * Compara esta ScoreEntry com outra para determinar a ordem.
     * As ScoreEntries são ordenadas pela pontuação em ordem decrescente.
     * @param other A ScoreEntry a ser comparada. Não deve ser nula.
     * @return Um inteiro negativo, zero ou positivo, consoante esta pontuação seja maior, igual ou menor que a pontuação especificada.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    /**
     * Devolve uma representação textual da ScoreEntry.
     * Formatada como "III ..... SSSSS", onde III são as iniciais e SSSSS é a pontuação.
     * @return Uma string formatada que representa a entrada da pontuação.
     */
    @Override
    public String toString() {
        return String.format("%-3S ..... %d", initials, score);
    }
}