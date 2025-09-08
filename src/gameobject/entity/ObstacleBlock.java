package gameobject.entity;

import gameobject.GameObject;
import gameobject.behaviour.ObstacleBehaviour;
import gameobject.collider.PolygonCollider;
import gameobject.geometry.Point;
import gameobject.shape.IShape;
import gameobject.transform.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * Representa um bloco de obstáculo estático no jogo.
 * É um GameObject com uma forma retangular personalizada para renderização e um colisor poligonal.
 * O seu comportamento é geralmente passivo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O nome (name) do obstáculo geralmente começa com "obstacle".
 * @inv A transformação (transform), colisor (collider), forma (shape) e comportamento (behaviour) nunca são nulos após a construção.
 * @inv O colisor é sempre um PolygonCollider.
 * @inv O comportamento é sempre um ObstacleBehaviour.
 */
public class ObstacleBlock extends GameObject {
    /**
     * Constrói um novo ObstacleBlock com um nome, posição e dimensões especificadas.
     * A posição (x,y) refere-se ao canto superior esquerdo do bloco.
     * @param name O nome do obstáculo (ex: "obstacle_L2_center"). Não deve ser nulo.
     * @param x A coordenada x inicial do canto superior esquerdo do obstáculo.
     * @param y A coordenada y inicial do canto superior esquerdo do obstáculo.
     * @param width A largura do obstáculo. Deve ser positiva.
     * @param height A altura do obstáculo. Deve ser positiva.
     * @post Um novo ObstacleBlock é criado na posição (x,y) com as dimensões (width, height) e o nome fornecido.
     * @post É inicializado com uma forma (IShape) personalizada para desenhar um retângulo com borda, um PolygonCollider correspondente às suas dimensões e um ObstacleBehaviour.
     */
    public ObstacleBlock(String name, double x, double y, int width, int height) {
        Transform transform = new Transform(x, y, 0, 0, 1);

        IShape shape = new IShape() {
            /**
             * Renderiza o bloco de obstáculo.
             * Desenha um retângulo preto com uma borda branca ligeiramente maior.
             * A renderização assume que drawX e drawY são o canto superior esquerdo do bloco.
             * @param g2 O contexto Graphics2D onde o obstáculo será desenhado. Não deve ser nulo.
             * @param drawX A coordenada x do canto superior esquerdo para desenhar o obstáculo.
             * @param drawY A coordenada y do canto superior esquerdo para desenhar o obstáculo.
             * @param angle O ângulo de rotação (ignorado para este obstáculo visualmente estático).
             * @param scale O fator de escala (ignorado para este obstáculo visualmente estático).
             * @param layer A camada de renderização (não utilizada diretamente).
             * @post O obstáculo é desenhado em g2 como um retângulo preto com borda branca.
             */
            @Override
            public void render(Graphics2D g2, int drawX, int drawY, double angle, double scale, int layer) {

                AffineTransform oldTransform = g2.getTransform();

                g2.setColor(Color.WHITE);
                g2.fillRect(drawX - 2, drawY - 2, width + 4, height + 4);

                g2.setColor(Color.BLACK);
                g2.fillRect(drawX, drawY, width, height);

                g2.setTransform(oldTransform);
            }
        };

        double[] colliderCoords = new double[]{
                0, 0,
                width, 0,
                width, height,
                0, height
        };

        PolygonCollider collider = new PolygonCollider(
                colliderCoords,
                transform
        );

        super(name, transform, collider, shape, new ObstacleBehaviour());
    }
}