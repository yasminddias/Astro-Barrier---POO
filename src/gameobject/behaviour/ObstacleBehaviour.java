package gameobject.behaviour;

import engine.IInputEvent;
import gameobject.IGameObject;

import java.util.List;

/**
 * Define o comportamento de um obstáculo estático no jogo.
 * Obstáculos são geralmente passivos e não têm lógica de atualização complexa nem reações a colisões
 * (exceto o facto de outros objetos poderem colidir com eles).
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O IGameObject associado (gameObject) pode ser nulo se não estiver associado.
 */
public class ObstacleBehaviour implements IBehaviour {
    private IGameObject gameObject;

    /**
     * Associa este comportamento a um IGameObject.
     * @param go O IGameObject a associar.
     * @post O campo 'gameObject' é definido para o IGameObject fornecido.
     */
    @Override
    public void gameObject(IGameObject go) {
        this.gameObject = go;
    }

    /**
     * Devolve o IGameObject ao qual este comportamento está associado.
     * @return O IGameObject associado.
     */
    @Override
    public IGameObject gameObject() {
        return gameObject;
    }

    /**
     * Método de inicialização. Não realiza ações para ObstacleBehaviour.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onInit() {}

    /**
     * Chamado quando o GameObject é ativado. Não realiza ações para ObstacleBehaviour.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onEnabled() {}

    /**
     * Chamado quando o GameObject é desativado. Não realiza ações para ObstacleBehaviour.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onDisabled() {}

    /**
     * Chamado quando o GameObject é destruído. Não realiza ações para ObstacleBehaviour.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onDestroy() {}

    /**
     * Método de atualização. Obstáculos são estáticos, por isso não há lógica de atualização.
     * @param dt Delta time (não utilizado).
     * @param input Inputs do jogo (não utilizado).
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onUpdate(double dt, IInputEvent input) {}

    /**
     * Trata colisões. Obstáculos são passivos, não reagem a colisões da sua parte.
     * @param others Lista de IGameObjects que colidiram (não utilizado).
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onCollision(List<IGameObject> others) {}
}