package tests;

import gameobject.geometry.Point;
import gameobject.transform.Transform;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Transform.
 * Verifica a inicialização da transformação e as operações de mover, rotacionar e escalar.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class TransformTest {
    private static final double DELTA = 1e-9; // Tolerância para comparações de double

    /**
     * Testa o construtor e os getters para os valores iniciais da transformação.
     * @post A posição, camada, ângulo e escala devem corresponder aos valores fornecidos ao construtor.
     */
    @Test
    void testConstructorAndGetters() {
        Transform t = new Transform(10.0, 20.0, 1, 45.0, 1.5); //
        assertEquals(10.0, t.position().getX(), DELTA, "Posição X inicial incorreta."); //
        assertEquals(20.0, t.position().getY(), DELTA, "Posição Y inicial incorreta."); //
        assertEquals(1, t.layer(), "Camada (layer) inicial incorreta."); //
        assertEquals(45.0, t.angle(), DELTA, "Ângulo inicial incorreto."); //
        assertEquals(1.5, t.scale(), DELTA, "Escala inicial incorreta."); //
    }

    /**
     * Testa o método move(Point, int).
     * @post A posição deve ser transladada e a camada deve ser ajustada.
     */
    @Test
    void testMove() {
        Transform t = new Transform(5.0, 5.0, 0, 0, 1.0); //
        Point dPos = new Point(3.0, -2.0); //
        t.move(dPos, 2); //

        assertEquals(8.0, t.position().getX(), DELTA, "Posição X incorreta após mover."); //
        assertEquals(3.0, t.position().getY(), DELTA, "Posição Y incorreta após mover."); //
        assertEquals(2, t.layer(), "Camada (layer) incorreta após mover."); //
    }

    /**
     * Testa o método rotate(double).
     * @post O ângulo da transformação deve ser incrementado.
     */
    @Test
    void testRotate() {
        Transform t = new Transform(0,0,0, 30.0, 1.0); //
        t.rotate(15.0); //
        assertEquals(45.0, t.angle(), DELTA, "Ângulo incorreto após rotacionar."); //

        t.rotate(-60.0); //
        assertEquals(-15.0, t.angle(), DELTA, "Ângulo incorreto após rotacionar negativamente."); //
    }

    /**
     * Testa o método scale(double).
     * @post A escala da transformação deve ser incrementada (ou decrementada).
     */
    @Test
    void testScale() {
        Transform t = new Transform(0,0,0,0, 1.0); //
        t.scale(0.5); //
        assertEquals(1.5, t.scale(), DELTA, "Escala incorreta após escalar positivamente."); //

        t.scale(-0.2); //
        assertEquals(1.3, t.scale(), DELTA, "Escala incorreta após escalar negativamente."); //
    }
}