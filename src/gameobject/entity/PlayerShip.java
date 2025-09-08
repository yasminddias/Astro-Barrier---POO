package gameobject.entity;

import gameobject.behaviour.PlayerBehaviour;
import gameobject.collider.CircleCollider;
import gameobject.shape.ShapeImage;
import gameobject.transform.ITransform;
import gameobject.transform.Transform;
import gameobject.GameObject;

/**
 * Representa a nave controlada pelo jogador.
 * É um GameObject com comportamento específico do jogador (movimento, disparo),
 * uma forma visual de nave e um colisor circular.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O nome (name) da nave do jogador é geralmente "player".
 * @inv A transformação (transform), colisor (collider), forma (shape) e comportamento (behaviour) nunca são nulos após a construção.
 * @inv O comportamento é sempre um PlayerBehaviour.
 */
public class PlayerShip extends GameObject {

    /**
     * Constrói um novo objeto PlayerShip com um nome e posição inicial especificados.
     * @param name O nome da nave do jogador (ex: "player"). Não deve ser nulo.
     * @param x A coordenada x inicial da nave do jogador.
     * @param y A coordenada y inicial da nave do jogador.
     * @post Uma nova PlayerShip é criada na posição (x,y) com o nome fornecido, usando um CircleCollider, uma ShapeImage ("assets/player.png") e um PlayerBehaviour.
     */
    public PlayerShip(String name, double x, double y) {
        super(
                name,
                new Transform(x, y, 0, 0, 1),
                new CircleCollider(x, y, 15, new Transform(x, y, 0, 0, 1)), // Nota: cria uma nova Transform para o colisor, idealmente deveria partilhar a mesma do GameObject
                new ShapeImage("assets/player.png"),
                new PlayerBehaviour()
        );
        // Correção Ideal: O CircleCollider deveria receber a mesma instância de ITransform que o GameObject
        // super(name, sharedTransform, new CircleCollider(x,y,15, sharedTransform), ...);
        // Onde 'sharedTransform' seria a new Transform(x,y,0,0,1)
        // A estrutura atual do construtor de GameObject e CircleCollider pode levar a dessincronização se
        // a transformação do GameObject for alterada sem que a transformação independente do colisor seja também atualizada,
        // a menos que CircleCollider.onUpdate() sincronize explicitamente com a transformação do GameObject associado (o que parece ser o caso).
    }
}