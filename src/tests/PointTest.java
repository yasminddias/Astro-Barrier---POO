package tests;

import gameobject.geometry.Point;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Point.
 * Verifica a criação de pontos, acesso a coordenadas, translação, rotação e representação em string.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class PointTest {

    private static final double DELTA = 1e-9; // Tolerância para comparações de double

    /**
     * Testa o construtor e os getters getX() e getY().
     * @post As coordenadas X e Y devem corresponder aos valores fornecidos ao construtor.
     */
    @Test
    void testConstructorAndGetters() {
        Point p = new Point(10.5, -5.2); //
        assertEquals(10.5, p.getX(), DELTA, "Coordenada X incorreta."); //
        assertEquals(-5.2, p.getY(), DELTA, "Coordenada Y incorreta."); //
    }

    /**
     * Testa o método set(double, double).
     * @post As coordenadas X e Y devem ser atualizadas para os novos valores.
     */
    @Test
    void testSetCoordinates() {
        Point p = new Point(1.0, 2.0); //
        p.set(3.0, 4.0); //
        assertEquals(3.0, p.getX(), DELTA, "Coordenada X deveria ser 3.0 após set."); //
        assertEquals(4.0, p.getY(), DELTA, "Coordenada Y deveria ser 4.0 após set."); //
    }

    /**
     * Testa o método set(Point).
     * @post As coordenadas X e Y devem ser copiadas do ponto fornecido.
     */
    @Test
    void testSetFromOtherPoint() {
        Point p1 = new Point(5.0, 6.0); //
        Point p2 = new Point(0, 0); //
        p2.set(p1); //
        assertEquals(5.0, p2.getX(), DELTA, "Coordenada X de p2 deveria ser 5.0."); //
        assertEquals(6.0, p2.getY(), DELTA, "Coordenada Y de p2 deveria ser 6.0."); //
    }

    /**
     * Testa o método set(Point) com um parâmetro nulo.
     * @post As coordenadas do ponto não devem ser alteradas se o parâmetro for nulo.
     */
    @Test
    void testSetFromNullPoint() {
        Point p = new Point(1.0, 2.0); //
        p.set(null); //
        assertEquals(1.0, p.getX(), DELTA, "Coordenada X não deveria mudar com set(null)."); //
        assertEquals(2.0, p.getY(), DELTA, "Coordenada Y não deveria mudar com set(null)."); //
    }

    /**
     * Testa o método translate(double, double).
     * @post As coordenadas X e Y devem ser incrementadas pelos valores de translação.
     */
    @Test
    void testTranslate() {
        Point p = new Point(3.0, 7.0); //
        p.translate(2.0, -3.0); //
        assertEquals(5.0, p.getX(), DELTA, "Coordenada X incorreta após translação."); //
        assertEquals(4.0, p.getY(), DELTA, "Coordenada Y incorreta após translação."); //
    }

    /**
     * Testa o método rotate(double, Point) com rotação de 90 graus.
     * @post O ponto (1,0) rotacionado 90 graus em torno da origem (0,0) deve tornar-se (0,1).
     */
    @Test
    void testRotate_90DegreesAroundOrigin() {
        Point p = new Point(1.0, 0.0); //
        Point origin = new Point(0.0, 0.0); //
        Point rotatedP = p.rotate(90.0, origin); //
        assertEquals(0.0, rotatedP.getX(), DELTA, "Coordenada X incorreta após rotação de 90 graus."); //
        assertEquals(1.0, rotatedP.getY(), DELTA, "Coordenada Y incorreta após rotação de 90 graus."); //
    }

    /**
     * Testa o método rotate(double, Point) com rotação em torno de um centroide diferente da origem.
     * @post O ponto (2,1) rotacionado -90 graus em torno de (1,1) deve tornar-se (1,0).
     */
    @Test
    void testRotate_AroundArbitraryCentroid() {
        Point p = new Point(2.0, 1.0); //
        Point centroid = new Point(1.0, 1.0); //
        Point rotatedP = p.rotate(-90.0, centroid); //
        assertEquals(1.0, rotatedP.getX(), DELTA, "Coordenada X incorreta após rotação em torno de centroide arbitrário."); //
        assertEquals(0.0, rotatedP.getY(), DELTA, "Coordenada Y incorreta após rotação em torno de centroide arbitrário."); //
    }

    /**
     * Testa o método rotate(double, Point) com rotação de 0 graus.
     * @post O ponto não deve mudar após uma rotação de 0 graus.
     */
    @Test
    void testRotate_0Degrees() {
        Point p = new Point(5.0, -5.0); //
        Point centroid = new Point(1.0, 1.0); //
        Point rotatedP = p.rotate(0.0, centroid); //
        assertEquals(5.0, rotatedP.getX(), DELTA, "Coordenada X não deveria mudar com rotação de 0 graus."); //
        assertEquals(-5.0, rotatedP.getY(), DELTA, "Coordenada Y não deveria mudar com rotação de 0 graus."); //
    }

    /**
     * Testa o método toString().
     * @post A representação em string deve ser "(x.xx,y.yy)".
     */
    @Test
    void testToString() {
        Point p = new Point(3.14159, -2.718); //
        assertEquals("(3.14,-2.72)", p.toString(), "Representação em string incorreta."); //
        Point p2 = new Point(7.0, 8.0); //
        assertEquals("(7.00,8.00)", p2.toString(), "Representação em string incorreta para inteiros."); //
    }
}