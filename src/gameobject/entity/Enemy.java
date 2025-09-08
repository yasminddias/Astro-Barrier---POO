package gameobject.entity;

import gameobject.GameObject;
import gameobject.behaviour.EnemyBehaviour;
import gameobject.collider.CircleCollider;
import gameobject.shape.ShapeImage;
import gameobject.transform.Transform;
import gameobject.path.EnemyPath;
import gui.Assets;

/**
 * Representa um inimigo no jogo.
 * É um GameObject com um comportamento específico de inimigo, que inclui seguir um caminho (EnemyPath),
 * uma forma visual e um colisor circular.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O nome (name) do inimigo geralmente começa com "enemy".
 * @inv A transformação (transform), colisor (collider), forma (shape) e comportamento (behaviour) nunca são nulos após a construção.
 * @inv O comportamento é sempre um EnemyBehaviour inicializado com um EnemyPath.
 */
public class Enemy extends GameObject {

    /**
     * Constrói um novo objeto Enemy com um nome, posição inicial e um caminho de movimento.
     * @param name O nome do inimigo (ex: "enemy1"). Não deve ser nulo.
     * @param x A coordenada x inicial do inimigo.
     * @param y A coordenada y inicial do inimigo.
     * @param path O EnemyPath que o inimigo seguirá. Pode ser nulo se o inimigo for estático, embora tipicamente seja fornecido.
     * @post Um novo Enemy é criado na posição (x,y) com o nome fornecido, usando um CircleCollider, uma ShapeImage (Assets.NORMAL_ENEMY) e um EnemyBehaviour inicializado com o 'path' fornecido.
     */
    public Enemy(String name, double x, double y, EnemyPath path) {
        Transform transform = new Transform(x, y, 0, 0, 1);

        super(
                name,
                transform,
                new CircleCollider(x, y, 15, transform),
                new ShapeImage(Assets.NORMAL_ENEMY),
                new EnemyBehaviour(path)
        );
    }
}