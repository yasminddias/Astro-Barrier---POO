package gameobject;

import gameobject.behaviour.IBehaviour;
import gameobject.collider.ICollider;
import gameobject.shape.IShape;
import gameobject.transform.ITransform;
import engine.GameEngine;

/**
 * Interface que define os métodos essenciais que um objeto de jogo deve implementar.
 * Permite acesso padronizado aos componentes principais de um GameObject, como
 * identificação, transformação, colisor, forma visual e comportamento.
 * Também gere a associação com o motor de jogo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Um IGameObject geralmente possui referências não nulas para transform, collider, shape e behaviour após a sua completa inicialização.
 * @see GameObject
 */
public interface IGameObject {

    /**
     * Devolve o nome do objeto de jogo.
     * @return String com o nome identificador do objeto.
     */
    String name();

    /**
     * Devolve o objeto de transformação (posição, rotação, escala, camada).
     * @return Instância de ITransform associada ao objeto.
     */
    ITransform transform();

    /**
     * Devolve o colisor associado ao objeto.
     * @return Instância de ICollider responsável pelas colisões do objeto.
     */
    ICollider collider();

    /**
     * Devolve a forma visual (IShape) associada ao objeto, usada para renderização.
     * @return Instância de IShape.
     */
    IShape shape();

    /**
     * Devolve o comportamento (IBehaviour) associado ao objeto, que define a sua lógica.
     * @return Instância de IBehaviour.
     */
    IBehaviour behaviour();

    /**
     * Devolve a instância do GameEngine à qual este objeto de jogo está associado.
     * Pode ser nulo se o objeto ainda não foi adicionado a um motor ou se opera independentemente.
     * @return A instância de GameEngine, ou nulo.
     */
    GameEngine engine();

    /**
     * Define a instância do GameEngine para este objeto de jogo.
     * Isto permite que o objeto interaja com o motor (ex: para se destruir ou adicionar outros objetos).
     * @param engine A instância de GameEngine a ser associada.
     * @post A referência interna ao motor de jogo é definida como 'engine'.
     */
    void setEngine(GameEngine engine);

    /**
     * Altera a forma visual (IShape) deste objeto de jogo.
     * @param newShape A nova IShape a ser usada para renderização. Não deve ser nula.
     * @post A forma visual do objeto é substituída por 'newShape'.
     */
    void changeShape(IShape newShape);
}