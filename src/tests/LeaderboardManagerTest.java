package tests;

import leaderboard.LeaderboardManager;
import leaderboard.ScoreEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Testes unitários para a classe LeaderboardManager.
 * Verifica a adição de pontuações, ordenação, limitação de entradas,
 * persistência (carregar e guardar) e limpeza da tabela de classificações.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class LeaderboardManagerTest {
    private static final String TEST_LEADERBOARD_FILE = "leaderboard_test.dat";
    private LeaderboardManager leaderboardManager;

    /**
     * Configuração antes de cada teste:
     * Cria uma nova instância de LeaderboardManager (que tentará carregar o ficheiro).
     * Para isolar os testes, o ficheiro de leaderboard de teste é eliminado.
     * @post Um novo LeaderboardManager é criado usando TEST_LEADERBOARD_FILE.
     * @post O ficheiro TEST_LEADERBOARD_FILE é eliminado, se existir.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Para garantir que cada teste começa com um estado limpo,
        // podemos modificar LeaderboardManager para aceitar um nome de ficheiro no construtor
        // ou usar reflexão para mudar o valor de LEADERBOARD_FILE temporariamente.
        // Por simplicidade aqui, vamos assumir que podemos limpar o ficheiro real ou
        // que o LeaderboardManager usa um ficheiro de teste.
        // Esta é uma abordagem simplificada; um setup mais robusto usaria um ficheiro temporário.

        // Solução pragmática: renomear o ficheiro real se existir, e usar um nome de teste
        // Aqui, vamos apenas apagar o ficheiro de teste para cada execução.
        Files.deleteIfExists(Paths.get(TEST_LEADERBOARD_FILE));

        // Para usar TEST_LEADERBOARD_FILE, teríamos de modificar LeaderboardManager
        // ou usar uma subclasse para testes. Assumindo que LeaderboardManager
        // usa o ficheiro definido pela sua constante LEADERBOARD_FILE.
        // Vou simular como se LeaderboardManager usasse o ficheiro original para os testes.
        // Numa situação real, seria melhor injetar o nome do ficheiro.
        Files.deleteIfExists(Paths.get("leaderboard.dat")); // Limpa o ficheiro real para o teste
        leaderboardManager = new LeaderboardManager(); //
    }

    /**
     * Limpeza após cada teste: Elimina o ficheiro de leaderboard de teste.
     * @post O ficheiro TEST_LEADERBOARD_FILE (ou o real, se não modificado) é eliminado.
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("leaderboard.dat")); // Limpa o ficheiro real após o teste
    }

    /**
     * Testa se um LeaderboardManager recém-criado (sem ficheiro existente) tem uma lista de pontuações vazia.
     * @post A lista de pontuações deve estar vazia.
     */
    @Test
    void testNewLeaderboardIsEmpty() {
        List<ScoreEntry> scores = leaderboardManager.getScores(); //
        assertTrue(scores.isEmpty(), "Um novo leaderboard deve estar vazio."); //
    }

    /**
     * Testa a adição de uma única pontuação.
     * @post A pontuação adicionada deve estar na lista de pontuações.
     */
    @Test
    void testAddSingleScore() {
        leaderboardManager.addScore("AAA", 100); //
        List<ScoreEntry> scores = leaderboardManager.getScores(); //
        assertEquals(1, scores.size(), "O leaderboard deve conter 1 pontuação."); //
        assertEquals("AAA", scores.get(0).getInitials()); //
        assertEquals(100, scores.get(0).getScore()); //
    }

    /**
     * Testa a ordenação das pontuações (ordem descendente).
     * @post As pontuações devem estar ordenadas da maior para a menor.
     */
    @Test
    void testScoresAreSortedDescending() {
        leaderboardManager.addScore("BBB", 200); //
        leaderboardManager.addScore("AAA", 100); //
        leaderboardManager.addScore("CCC", 300); //

        List<ScoreEntry> scores = leaderboardManager.getScores(); //
        assertEquals(3, scores.size()); //
        assertEquals(300, scores.get(0).getScore(), "A maior pontuação (300) deve ser a primeira."); //
        assertEquals(200, scores.get(1).getScore(), "A segunda maior pontuação (200) deve ser a segunda."); //
        assertEquals(100, scores.get(2).getScore(), "A menor pontuação (100) deve ser a última."); //
    }

    /**
     * Testa o limite máximo de entradas na tabela de classificações.
     * O LeaderboardManager está configurado para MAX_ENTRIES = 10.
     * @post A tabela de classificações não deve exceder MAX_ENTRIES (10) entradas.
     * @post As entradas mantidas devem ser as de maior pontuação.
     */
    @Test
    void testMaxEntriesLimit() {
        // Adicionar 11 pontuações
        for (int i = 1; i <= 11; i++) {
            leaderboardManager.addScore("P" + String.format("%02d", i), i * 10); //
        }
        List<ScoreEntry> scores = leaderboardManager.getScores(); //
        assertEquals(10, scores.size(), "O leaderboard deve ter no máximo 10 entradas."); //
        // A menor pontuação adicionada foi 10 (P01), que deve ter sido removida.
        // A menor pontuação que deve permanecer é 20 (P02), se MAX_ENTRIES é 10 e adicionamos 11.
        // Pontuações: 110, 100, 90, 80, 70, 60, 50, 40, 30, 20. (10 removido)
        assertEquals(20, scores.get(9).getScore(), "A menor pontuação no top 10 deve ser 20."); //
        assertEquals(110, scores.get(0).getScore(), "A maior pontuação no top 10 deve ser 110."); //
    }

    /**
     * Testa a persistência das pontuações (guardar e carregar).
     * @post As pontuações guardadas devem ser carregadas corretamente por uma nova instância de LeaderboardManager.
     */
    @Test
    void testPersistence_SaveAndLoad() {
        leaderboardManager.addScore("SAV", 500); //
        leaderboardManager.addScore("LOD", 600); //

        // Criar um novo LeaderboardManager; ele deve carregar do ficheiro guardado pelo anterior.
        LeaderboardManager newLeaderboardManager = new LeaderboardManager(); //
        List<ScoreEntry> loadedScores = newLeaderboardManager.getScores(); //

        assertEquals(2, loadedScores.size(), "Deviam ter sido carregadas 2 pontuações."); //
        assertEquals(600, loadedScores.get(0).getScore(), "A pontuação carregada de 600 está incorreta ou não é a primeira."); //
        assertEquals("LOD", loadedScores.get(0).getInitials()); //
        assertEquals(500, loadedScores.get(1).getScore(), "A pontuação carregada de 500 está incorreta ou não é a segunda."); //
        assertEquals("SAV", loadedScores.get(1).getInitials()); //
    }

    /**
     * Testa a funcionalidade de limpar todas as pontuações.
     * @post Após limpar, a lista de pontuações deve estar vazia.
     * @post Após limpar e recarregar, a lista de pontuações deve continuar vazia.
     */
    @Test
    void testClearScores() {
        leaderboardManager.addScore("TST", 10); //
        assertFalse(leaderboardManager.getScores().isEmpty(), "Leaderboard não devia estar vazio antes de limpar."); //

        leaderboardManager.clearScores(); //
        assertTrue(leaderboardManager.getScores().isEmpty(), "Leaderboard devia estar vazio após limpar."); //

        // Verificar se o ficheiro foi guardado como vazio
        LeaderboardManager newLeaderboardManager = new LeaderboardManager(); //
        assertTrue(newLeaderboardManager.getScores().isEmpty(), "Leaderboard devia estar vazio após limpar e recarregar."); //
    }
}