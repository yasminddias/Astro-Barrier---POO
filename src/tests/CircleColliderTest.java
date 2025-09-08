package tests;

import gameobject.collider.CircleCollider;
import gameobject.collider.PolygonCollider;
import gameobject.geometry.Point;
import gameobject.transform.Transform;
import gameobject.transform.ITransform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Testes unitários para a classe CircleCollider.
 * Verifica a deteção de colisões entre CircleColliders, e entre CircleCollider e PolygonCollider.
 * Testa também a atualização do colisor com base na sua transformação.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 */
class CircleColliderTest {

    private ITransform transform1, transform2, transformPoly;
    private CircleCollider circle1, circle2, circleSeparated, circleTouching;
    private PolygonCollider squarePolygon; // Para testar colisão círculo-polígono

    private static final double DELTA = 1e-9;

    /**
     * Configuração inicial antes de cada teste.
     * Cria instâncias de Transform e CircleCollider para os testes.
     * @post transform1, transform2, circle1, circle2, circleSeparated, circleTouching,
     * transformPoly e squarePolygon são inicializados.
     */
    @BeforeEach
    void setUp() {
        // Círculos para teste círculo-círculo
        transform1 = new Transform(0, 0, 0, 0, 1.0); //
        circle1 = new CircleCollider(0, 0, 10, transform1); // Raio 10, centro na origem
        circle1.onUpdate(); // Sincroniza com a transform

        transform2 = new Transform(15, 0, 0, 0, 1.0); //
        circle2 = new CircleCollider(15, 0, 10, transform2); // Raio 10, centro em (15,0) - deve colidir com circle1
        circle2.onUpdate(); //

        ITransform transformSeparated = new Transform(25, 0, 0, 0, 1.0); //
        circleSeparated = new CircleCollider(25, 0, 4, transformSeparated); // Raio 4, centro em (25,0) - não deve colidir com circle1
        circleSeparated.onUpdate(); //

        ITransform transformTouching = new Transform(20, 0, 0, 0, 1.0); //
        circleTouching = new CircleCollider(20, 0, 10, transformTouching); // Raio 10, centro em (20,0) - deve tocar circle1 (colisão)
        circleTouching.onUpdate(); //

        // Polígono para teste círculo-polígono
        transformPoly = new Transform(30, 0, 0, 0, 1.0); //
        double[] squareCoords = {
                -5, -5,
                5, -5,
                5,  5,
                -5,  5
        };
        squarePolygon = new PolygonCollider(squareCoords, transformPoly); //
        squarePolygon.onUpdate(); // Sincroniza com a transform
    }

    /**
     * Testa a atualização do centro e raio do CircleCollider com base na sua Transform.
     * @post O centro e o raio do colisor devem refletir a posição e escala da sua Transform.
     */
    @Test
    void testOnUpdate_SyncWithTransform() {
        ITransform transformMovel = new Transform(5, 10, 0, 0, 2.0); //
        CircleCollider circuloMovel = new CircleCollider(0, 0, 5, transformMovel); // Raio original 5

        circuloMovel.onUpdate(); // Sincroniza com a transform

        assertEquals(5, circuloMovel.centroid().getX(), DELTA, "Centro X deve ser 5 após onUpdate."); //
        assertEquals(10, circuloMovel.centroid().getY(), DELTA, "Centro Y deve ser 10 após onUpdate."); //
        assertEquals(10, circuloMovel.getRadius(), DELTA, "Raio deve ser 10 (5 * 2.0) após onUpdate."); //
        assertEquals(10, circuloMovel.getCharacteristicDimension(), DELTA, "Dimensão característica (raio escalado) deve ser 10."); //

        transformMovel.move(new Point(1, 1), 0); //
        transformMovel.scale(0.5); // Escala da transform torna-se 2.0 + 0.5 = 2.5

        circuloMovel.onUpdate(); // Sincroniza novamente
        assertEquals(6, circuloMovel.centroid().getX(), DELTA, "Centro X deve ser 6 após mover."); //
        assertEquals(11, circuloMovel.centroid().getY(), DELTA, "Centro Y deve ser 11 após mover."); //
        assertEquals(5 * transformMovel.scale(), circuloMovel.getRadius(), DELTA, "Raio deve ser originalRadius * novaEscalaDaTransform."); //
    }

    /**
     * Testa a colisão entre dois CircleColliders que se intersetam.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_CirclesIntersecting() {
        assertTrue(circle1.isColliding(circle2), "Círculos 1 e 2 (intersetando-se) deviam colidir."); //
        assertTrue(circle2.isColliding(circle1), "Colisão deve ser simétrica (Círculo 2 com 1)."); //
    }

    /**
     * Testa a ausência de colisão entre dois CircleColliders separados.
     * @post isColliding deve devolver falso.
     */
    @Test
    void testIsColliding_CirclesSeparated() {
        assertFalse(circle1.isColliding(circleSeparated), "Círculos 1 e circleSeparated (separados) não deviam colidir."); //
        assertFalse(circleSeparated.isColliding(circle1), "Ausência de colisão deve ser simétrica."); //
    }

    /**
     * Testa a colisão de um círculo consigo mesmo (deve ser verdadeiro se tiver raio > 0).
     * @post Um círculo deve colidir consigo mesmo.
     */
    @Test
    void testIsColliding_CircleWithItself() {
        assertTrue(circle1.isColliding(circle1), "Um círculo deve colidir consigo mesmo."); //
    }

    /**
     * Testa a colisão entre um CircleCollider e um PolygonCollider (quadrado) quando intersetam.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_CircleAndPolygon_Intersecting() {
        transformPoly.position().set(5,0); //
        squarePolygon.onUpdate(); //
        assertTrue(circle1.isColliding(squarePolygon), "Círculo e Polígono (intersetando-se) deviam colidir."); //
        assertTrue(squarePolygon.isColliding(circle1), "Colisão Polígono-Círculo deve ser simétrica."); //
    }

    /**
     * Testa a colisão entre um CircleCollider e um PolygonCollider (quadrado) quando o círculo está completamente dentro do polígono.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_CircleInsidePolygon() {
        transform1.position().set(30,0); //
        transform1.scale(-0.5);  //
        circle1.onUpdate(); //
        assertTrue(circle1.isColliding(squarePolygon), "Círculo dentro do Polígono devia colidir."); //
        assertTrue(squarePolygon.isColliding(circle1), "Colisão Polígono-Círculo (círculo dentro) deve ser simétrica."); //
    }

    /**
     * Testa a colisão entre um CircleCollider e um PolygonCollider (quadrado) quando o polígono está completamente dentro do círculo.
     * @post isColliding deve devolver verdadeiro.
     */
    @Test
    void testIsColliding_PolygonInsideCircle() {
        transformPoly.position().set(0,0); //
        transformPoly.scale(-0.5); //
        squarePolygon.onUpdate(); //
        assertTrue(circle1.isColliding(squarePolygon), "Polígono dentro do Círculo devia colidir."); //
        assertTrue(squarePolygon.isColliding(circle1), "Colisão Polígono-Círculo (polígono dentro) deve ser simétrica."); //
    }

    /**
     * Testa a ausência de colisão entre um CircleCollider e um PolygonCollider (quadrado) quando estão separados.
     * @post isColliding deve devolver falso.
     */
    @Test
    void testIsColliding_CircleAndPolygon_Separated() {
        assertFalse(circle1.isColliding(squarePolygon), "Círculo e Polígono (separados) não deviam colidir."); //
        assertFalse(squarePolygon.isColliding(circle1), "Ausência de colisão Polígono-Círculo deve ser simétrica."); //
    }
}