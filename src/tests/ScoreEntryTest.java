package tests;

import leaderboard.ScoreEntry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe ScoreEntry.
 * Verifica a sanitização das iniciais, a atribuição da pontuação,
 * a comparação entre entradas e a representação em string.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class ScoreEntryTest {

    /**
     * Testa o construtor e os getters para iniciais e pontuação válidas.
     * @post As iniciais devem ser "ABC" e a pontuação 100.
     */
    @Test
    void testConstructorAndGetters_ValidInput() {
        ScoreEntry entry = new ScoreEntry("ABC", 100); //
        assertEquals("ABC", entry.getInitials(), "As iniciais devem ser ABC."); //
        assertEquals(100, entry.getScore(), "A pontuação deve ser 100."); //
    }

    /**
     * Testa a sanitização das iniciais: trim, toUpperCase, e comprimento máximo.
     * @post As iniciais devem ser "DEF" após sanitização de " defghi ".
     */
    @Test
    void testInitialsSanitization_TrimUpperMax() {
        ScoreEntry entry = new ScoreEntry(" defghi ", 200); //
        assertEquals("DEF", entry.getInitials(), "As iniciais devem ser DEF após trim, toUpperCase e corte."); //
    }

    /**
     * Testa a sanitização para iniciais nulas.
     * @post As iniciais devem ser "???" para input nulo.
     */
    @Test
    void testInitialsSanitization_NullInput() {
        ScoreEntry entry = new ScoreEntry(null, 50); //
        assertEquals("???", entry.getInitials(), "As iniciais devem ser ??? para input nulo."); //
    }

    /**
     * Testa a sanitização para iniciais vazias.
     * @post As iniciais devem ser "???" para input vazio.
     */
    @Test
    void testInitialsSanitization_EmptyInput() {
        ScoreEntry entry = new ScoreEntry("", 75); //
        assertEquals("???", entry.getInitials(), "As iniciais devem ser ??? para input vazio."); //
        ScoreEntry entrySpaces = new ScoreEntry("   ", 80); //
        assertEquals("???", entrySpaces.getInitials(), "As iniciais devem ser ??? para input apenas com espaços."); //
    }

    /**
     * Testa a sanitização para iniciais com menos de 3 caracteres.
     * @post As iniciais devem ser "AB" para input "ab".
     */
    @Test
    void testInitialsSanitization_LessThanThreeChars() {
        ScoreEntry entry = new ScoreEntry("ab", 120); //
        assertEquals("AB", entry.getInitials(), "As iniciais devem ser AB."); //
    }

    /**
     * Testa o método compareTo para ordenação descendente de pontuações.
     * @post entry1 (pontuação maior) deve vir antes de entry2 (pontuação menor).
     * @post entry2 deve vir depois de entry1.
     * @post entry1 e entry3 (mesma pontuação) devem ser considerados iguais na ordenação.
     */
    @Test
    void testCompareTo() {
        ScoreEntry entry1 = new ScoreEntry("P1", 200); //
        ScoreEntry entry2 = new ScoreEntry("P2", 100); //
        ScoreEntry entry3 = new ScoreEntry("P3", 200); //

        assertTrue(entry1.compareTo(entry2) < 0, "Entry1 (200) deve ser menor que entry2 (100) para ordenação descendente."); //
        assertTrue(entry2.compareTo(entry1) > 0, "Entry2 (100) deve ser maior que entry1 (200) para ordenação descendente."); //
        assertEquals(0, entry1.compareTo(entry3), "Entry1 (200) e Entry3 (200) devem ser iguais na comparação."); //
    }

    /**
     * Testa a representação em String da ScoreEntry.
     * @post A string deve seguir o formato "INI ..... PONTOS".
     */
    @Test
    void testToString() {
        ScoreEntry entry = new ScoreEntry("XYZ", 12345); //
        assertEquals("XYZ ..... 12345", entry.toString(), "A representação em string está incorreta."); //
    }
}