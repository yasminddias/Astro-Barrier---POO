package gameobject.entity;

import gameobject.GameObject;
import gameobject.behaviour.BulletBehaviour;
import gameobject.collider.CircleCollider;
import gameobject.shape.ShapeImage;
import gameobject.transform.Transform;

/**
 * Representa um projétil no jogo, tipicamente disparado pelo jogador.
 * É um GameObject com comportamento, forma e colisor específicos para projéteis.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O nome (name) do projétil geralmente começa com "player_bullet_".
 * @inv A transformação (transform), colisor (collider), forma (shape) e comportamento (behaviour) nunca são nulos após a construção.
 */
public class Bullet extends GameObject {

    /**
     * Constrói um novo objeto Bullet com um nome e posição especificados.
     * Utiliza uma transformação partilhada criada internamente.
     * @param name O nome do projétil (ex: "player_bullet_1"). Não deve ser nulo.
     * @param x A coordenada x inicial do projétil.
     * @param y A coordenada y inicial do projétil.
     * @post Um novo Bullet é criado na posição (x,y) com o nome fornecido, usando um CircleCollider, ShapeImage e BulletBehaviour predefinidos.
     */
    public Bullet(String name, double x, double y) {
        this(name, createSharedTransform(x, y));
    }

    /**
     * Cria e devolve um novo objeto Transform para o projétil.
     * Este método é usado para encapsular a criação da transformação.
     * @param x A coordenada x para a nova transformação.
     * @param y A coordenada y para a nova transformação.
     * @return Um novo objeto Transform inicializado na posição (x,y), camada 0, ângulo 0 e escala 1.
     */
    private static Transform createSharedTransform(double x, double y) {
        return new Transform(x, y, 0, 0, 1);
    }

    /**
     * Construtor privado que inicializa o projétil com um nome e uma transformação fornecida.
     * Este construtor é chamado pelo construtor público.
     * @param name O nome do projétil. Não deve ser nulo.
     * @param t A transformação a ser usada pelo projétil. Não deve ser nula.
     * @post Um novo Bullet é criado com os componentes (colisor, forma, comportamento) associados à transformação 't'.
     */
    private Bullet(String name, Transform t) {
        super(
                name,
                t,
                new CircleCollider(t.position().getX(), t.position().getY(), 5, t),
                new ShapeImage("assets/bullet.png"),
                new BulletBehaviour()
        );
    }
}