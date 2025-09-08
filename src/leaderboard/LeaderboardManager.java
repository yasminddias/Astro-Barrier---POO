package leaderboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gere a tabela de classificação (leaderboard) do jogo.
 * Responsável por carregar, guardar, adicionar e limpar pontuações.
 * Assegura que a tabela mantém um número máximo de entradas e que as pontuações estão ordenadas.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A lista 'scores' nunca é nula.
 * @inv A lista 'scores' contém sempre objetos ScoreEntry ordenados por pontuação em ordem decrescente.
 * @inv O número de entradas em 'scores' não excede MAX_ENTRIES após uma operação de adição.
 */
public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static final int MAX_ENTRIES = 10;
    private List<ScoreEntry> scores;

    /**
     * Constrói um LeaderboardManager e inicializa a lista de pontuações,
     * carregando quaisquer pontuações persistidas.
     * @post Um novo LeaderboardManager é criado e as pontuações são carregadas de LEADERBOARD_FILE, se existir;
     * caso contrário, uma tabela de classificação vazia é inicializada. A lista de pontuações é ordenada.
     */
    public LeaderboardManager() {
        scores = new ArrayList<>();
        loadScores();
    }

    /**
     * Devolve uma visualização não modificável das pontuações atuais.
     * @return Uma lista não modificável de objetos ScoreEntry que representa a tabela de classificação atual.
     */
    public List<ScoreEntry> getScores() {
        return Collections.unmodifiableList(new ArrayList<>(scores));
    }

    /**
     * Adiciona uma nova entrada de pontuação à tabela de classificação.
     * A lista é subsequentemente ordenada, truncada se exceder o número máximo de entradas, e guardada.
     * @param initials As iniciais do jogador (não devem ser nulas; serão aparadas e convertidas para maiúsculas).
     * @param score A pontuação alcançada pelo jogador (deve ser um inteiro não negativo).
     * @post A nova pontuação é adicionada à lista 'scores'.
     * @post A lista 'scores' é ordenada em ordem decrescente por pontuação.
     * @post Se o tamanho da lista 'scores' exceder MAX_ENTRIES, é truncada para MAX_ENTRIES.
     * @post A lista de pontuações atualizada é persistida em LEADERBOARD_FILE.
     */
    public void addScore(String initials, int score) {
        scores.add(new ScoreEntry(initials, score));
        Collections.sort(scores);
        if (scores.size() > MAX_ENTRIES) {
            scores = new ArrayList<>(scores.subList(0, MAX_ENTRIES));
        }
        saveScores();
    }

    /**
     * Remove todas as pontuações da tabela de classificação e guarda o estado vazio.
     * @post A lista 'scores' fica vazia.
     * @post A tabela de classificação vazia é persistida em LEADERBOARD_FILE.
     */
    public void clearScores() {
        scores.clear();
        saveScores();
        System.out.println("Leaderboard cleared.");
    }

    /**
     * Carrega as pontuações do ficheiro LEADERBOARD_FILE.
     * Se o ficheiro não existir, estiver corrompido ou contiver dados incompatíveis,
     * uma tabela de classificação vazia é inicializada. As pontuações carregadas são ordenadas.
     * @post A lista 'scores' é preenchida com dados de LEADERBOARD_FILE, ou inicializada como uma lista vazia.
     * @post A lista 'scores' é ordenada.
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(LEADERBOARD_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
                Object loadedObject = ois.readObject();
                try {
                    scores = (List<ScoreEntry>) loadedObject;
                    if (scores == null) {
                        scores = new ArrayList<>();
                    }
                } catch (ClassCastException e) {
                    System.err.println("Leaderboard file contains incompatible data type: " + loadedObject.getClass().getName() + ". Resetting leaderboard.");
                    scores = new ArrayList<>();
                }
            } catch (FileNotFoundException e) {
                System.err.println("Leaderboard file not found. A new one will be created on save.");
                scores = new ArrayList<>();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading leaderboard: " + e.getMessage());
                e.printStackTrace();
                scores = new ArrayList<>();
            }
        } else {
            System.out.println("No leaderboard file found. Starting with an empty leaderboard.");
            scores = new ArrayList<>();
        }
        Collections.sort(scores);
    }

    /**
     * Guarda a lista atual de pontuações no ficheiro LEADERBOARD_FILE.
     * @post O estado atual da lista 'scores' é persistido em LEADERBOARD_FILE.
     * Se ocorrer uma IOException, uma mensagem de erro é impressa.
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(new ArrayList<>(scores));
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}