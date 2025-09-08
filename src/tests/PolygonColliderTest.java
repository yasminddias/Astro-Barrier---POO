package tests;

import gameobject.collider.CircleCollider;
import gameobject.collider.PolygonCollider;
import gameobject.geometry.Point;
import gameobject.transform.Transform;
import gameobject.transform.ITransform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Testes unitários para a classe PolygonCollider.
 * Verifica a atualização dos vértices transformados, cálculo do centroide,
 * e deteção de colisões entre PolygonColliders e entre PolygonCollider e CircleCollider.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class PolygonColliderTest {

    private ITransform transformPoly1, transformPoly2, transformCircle;
    private PolygonCollider poly1_square, poly2_square_offset, poly_triangle;
    private CircleCollider circle_collider;

    private static final double DELTA = 1e-9;

    /**
     * Configuração inicial antes de cada teste.
     * Cria instâncias de Transform, PolygonCollider e CircleCollider.
     * @post Variáveis de colisor e transformação são inicializadas.
     */
    @BeforeEach
    void setUp() {
        // Polígono 1: Quadrado centrado na origem (0,0) inicialmente
        transformPoly1 = new Transform(0, 0, 0, 0, 1.0); //
        double[] squareCoords1 = {-10, -10, 10, -10, 10, 10, -10, 10}; // Vértices locais
        poly1_square = new PolygonCollider(squareCoords1, transformPoly1); //
        poly1_square.onUpdate(); //

        // Polígono 2: Mesmo quadrado, mas deslocado para colidir com o primeiro
        transformPoly2 = new Transform(15, 0, 0, 0, 1.0); // Deslocado 15 em X
        poly2_square_offset = new PolygonCollider(squareCoords1, transformPoly2); //
        poly2_square_offset.onUpdate(); //

        // Polígono 3: Triângulo
        ITransform transformTriangle = new Transform(5, 5, 0, 0, 1.0); //
        double[] triangleCoords = {0,10, -10,-10, 10,-10}; // Triângulo com base em baixo
        poly_triangle = new PolygonCollider(triangleCoords, transformTriangle); //
        poly_triangle.onUpdate(); //

        // Círculo para testes de colisão polígono-círculo
        transformCircle = new Transform(5, 0, 0, 0, 1.0); //
        circle_collider = new CircleCollider(5, 0, 8, transformCircle); // Raio 8
        circle_collider.onUpdate(); //
    }

    /**
     * Testa se os vértices transformados são calculados corretamente após onUpdate.
     * @post Os vértices em 'transformedVertices' devem refletir a posição, rotação e escala da 'transform'.
     */
    @Test
    void testOnUpdate_TransformedVertices() {
        ITransform t = new Transform(10, 20, 0, 90, 2.0); // Posição (10,20), rotação 90 graus, escala 2x
        double[] localRect = {-1, -1, 1, -1, 1, 1, -1, 1}; // Quadrado local de 2x2 centrado na origem
        PolygonCollider p = new PolygonCollider(localRect, t); //
        p.onUpdate(); //
        List<Point> transformed = p.getVertices(); //

        // Vértice local (-1, -1) * escala 2.0 = (-2, -2)
        // Rotacionar (-2,-2) por 90 graus -> (2, -2)
        // Transladar por (10,20) -> (10+2, 20-2) = (12, 18)
        assertEquals(12.0, transformed.get(0).getX(), DELTA, "Vértice 0 X transformado incorreto."); //
        assertEquals(18.0, transformed.get(0).getY(), DELTA, "Vértice 0 Y transformado incorreto."); //

        // Vértice local (1, -1) * escala 2.0 = (2, -2)
        // Rotacionar (2,-2) por 90 graus -> (2, 2)
        // Transladar por (10,20) -> (10+2, 20+2) = (12, 22)
        assertEquals(12.0, transformed.get(1).getX(), DELTA, "Vértice 1 X transformado incorreto."); //
        assertEquals(22.0, transformed.get(1).getY(), DELTA, "Vértice 1 Y transformado incorreto."); //
    }

    /**
     * Testa o cálculo do centroide de um polígono simples (quadrado centrado na origem).
     * @post O centroide deve ser (0,0) para um quadrado simétrico centrado na origem da sua transform.
     */
    @Test
    void testCentroid_SimpleSquare() {
        // poly1_square está com transform na origem (0,0)
        Point centroid = poly1_square.centroid(); //
        assertEquals(0.0, centroid.getX(), DELTA, "Centroide X de poly1_square deve ser 0."); //
        assertEquals(0.0, centroid.getY(), DELTA, "Centroide Y de poly1_square deve ser 0."); //

        // Mover a transform do poly1_square
        transformPoly1.move(new Point(5, -5), 0); //
        poly1_square.onUpdate(); // Atualizar vértices transformados
        centroid = poly1_square.centroid(); // Recalcular centroide
        assertEquals(5.0, centroid.getX(), DELTA, "Centroide X de poly1_square deve ser 5 após mover transform."); //
        assertEquals(-5.0, centroid.getY(), DELTA, "Centroide Y de poly1_square deve ser -5 após mover transform."); //
    }

    /**
     * Testa a colisão entre dois PolygonColliders (quadrados) que se intersetam.
     * poly1_square: centro (0,0), X de -10 a 10.
     * poly2_square_offset: centro (15,0), X de (15-10)=5 a (15+10)=25.
     * Há interseção entre X=5 e X=10.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_PolygonsIntersecting() {
        assertTrue(poly1_square.isColliding(poly2_square_offset), "Polígonos 1 e 2 (intersetando-se) deviam colidir."); //
        assertTrue(poly2_square_offset.isColliding(poly1_square), "Colisão Polígono-Polígono deve ser simétrica."); //
    }

    /**
     * Testa a ausência de colisão entre dois PolygonColliders (quadrados) separados.
     * Move poly2_square_offset para que não haja interseção.
     * @post isColliding deve devolver falso.
     */
    @Test
    void testIsColliding_PolygonsSeparated() {
        transformPoly2.position().set(30, 0); // Move poly2 para (30,0). X irá de 20 a 40. poly1 X de -10 a 10.
        poly2_square_offset.onUpdate(); //

        assertFalse(poly1_square.isColliding(poly2_square_offset), "Polígonos 1 e 2 (separados) não deviam colidir."); //
        assertFalse(poly2_square_offset.isColliding(poly1_square), "Ausência de colisão Polígono-Polígono deve ser simétrica."); //
    }

    /**
     * Testa a colisão de um polígono consigo mesmo.
     * @post Um polígono deve colidir consigo mesmo.
     */
    @Test
    void testIsColliding_PolygonWithItself() {
        assertTrue(poly1_square.isColliding(poly1_square), "Um polígono devia colidir consigo mesmo."); //
    }

    /**
     * Testa a colisão entre um PolygonCollider (quadrado) e um CircleCollider quando intersetam.
     * poly1_square: X de -10 a 10, Y de -10 a 10.
     * circle_collider: centro (5,0), raio 8. X do círculo de (5-8)=-3 a (5+8)=13.
     * Há interseção.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_PolygonAndCircle_Intersecting() {
        // poly1_square está em (0,0), X de -10 a 10, Y de -10 a 10
        // circle_collider está em (5,0) com raio 8
        assertTrue(poly1_square.isColliding(circle_collider), "Polígono e Círculo (intersetando-se) deviam colidir."); //
        assertTrue(circle_collider.isColliding(poly1_square), "Colisão Círculo-Polígono deve ser simétrica."); //
    }

    /**
     * Testa a ausência de colisão entre um PolygonCollider (quadrado) e um CircleCollider quando estão separados.
     * poly1_square: X de -10 a 10.
     * Move o círculo para (20,0), raio 8. X do círculo de 12 a 28. Não há colisão.
     * @post isColliding deve devolver falso.
     */
    @Test
    void testIsColliding_PolygonAndCircle_Separated() {
        transformCircle.position().set(20, 0); // Move o círculo para (20,0)
        circle_collider.onUpdate(); //
        // poly1_square em (0,0), X de -10 a 10.
        // circle_collider em (20,0), raio 8. X de 12 a 28. Estão separados.

        assertFalse(poly1_square.isColliding(circle_collider), "Polígono e Círculo (separados) não deviam colidir."); //
        assertFalse(circle_collider.isColliding(poly1_square), "Ausência de colisão Círculo-Polígono deve ser simétrica."); //
    }

    /**
     * Testa a colisão entre um triângulo e um círculo.
     * Triângulo: (0,10), (-10,-10), (10,-10) com transform em (5,5)
     * Vértices transformados: (5,15), (-5,-5), (15,-5)
     * Círculo: centro (5,0), raio 8.
     * O vértice (-5,-5) do triângulo (que no mundo é (5-10, 5-10) = (-5, -5)) está a uma distância de sqrt((5 - (-5))^2 + (0 - (-5))^2) = sqrt(100+25) = sqrt(125) ~ 11 do centro do círculo.
     * Isto precisa de uma análise geométrica mais cuidada.
     * O ponto (5, -5) do triângulo (transformado) estará perto do círculo.
     * O ponto (5,5) (vértice superior do triângulo no mundo) está a dist 5 do centro do circulo (5,0). Raio 8 -> colide.
     * @post Devem colidir.
     */
    @Test
    void testIsColliding_TriangleAndCircle() {
        // poly_triangle (transform em (5,5)): vértices locais {0,10, -10,-10, 10,-10}
        // Vértices no mundo: (5,15), (-5,-5), (15,-5)
        // circle_collider (transform em (5,0)): centro (5,0), raio 8

        // Vértice superior do triângulo: (5, 15)
        // Centro do círculo: (5,0)
        // Distância entre (5,15) e (5,0) é 15. Raio é 8. Não colide por este vértice.

        // Ponto médio da base do triângulo: x=5, y=( (5-10) + (5-10) )/2 = -5. Ponto no mundo (5,-5)
        // Distância de (5,-5) ao centro do círculo (5,0) é 5. Raio é 8. Colide.

        assertTrue(poly_triangle.isColliding(circle_collider), "Triângulo e Círculo deviam colidir neste cenário."); //
        assertTrue(circle_collider.isColliding(poly_triangle), "Colisão Círculo-Triângulo devia ser simétrica."); //
    }
}