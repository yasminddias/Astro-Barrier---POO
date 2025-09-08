package gameobject.behaviour;

import engine.IInputEvent;
import gameobject.IGameObject;
import gameobject.geometry.Point;

import java.awt.*;
import java.util.List;

/**
 * Define o comportamento de um projétil no jogo.
 * Responsável pelo movimento linear do projétil e pela sua destruição
 * ao colidir com inimigos/obstáculos ou ao sair dos limites do ecrã.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A velocidade (speed) do projétil é constante e negativa (move-se para cima no ecrã).
 * @inv O IGameObject associado (go) deve existir para que o comportamento funcione.
 */
public class BulletBehaviour implements IBehaviour {
    private IGameObject go;
    private final double speed = -300.0; // Velocidade para cima

    /**
     * Devolve o IGameObject ao qual este comportamento está associado.
     * @return O IGameObject associado.
     */
    @Override
    public IGameObject gameObject() {
        return go;
    }

    /**
     * Associa este comportamento a um IGameObject.
     * @param go O IGameObject a associar.
     * @post O campo 'go' é definido para o IGameObject fornecido.
     */
    @Override
    public void gameObject(IGameObject go) {
        this.go = go;
    }

    /**
     * Método de inicialização do comportamento. Não realiza ações específicas para BulletBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override
    public void onInit() {}

    /**
     * Chamado quando o GameObject associado é ativado. Não realiza ações específicas para BulletBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override
    public void onEnabled() {}

    /**
     * Chamado quando o GameObject associado é desativado. Não realiza ações específicas para BulletBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override
    public void onDisabled() {}

    /**
     * Chamado quando o GameObject associado é destruído. Não realiza ações específicas para BulletBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override
    public void onDestroy() {}

    /**
     * Atualiza a posição do projétil em cada frame.
     * Move o projétil verticalmente para cima. Se sair dos limites superiores do motor de jogo, é destruído.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param input O estado atual dos inputs (não utilizado por este comportamento).
     * @post A posição Y do IGameObject 'go' é decrementada (movimento para cima) com base na 'speed' e 'dt'.
     * @post Se 'go' sair do limite superior do ecrã definido no motor de jogo, 'go' é destruído através do motor.
     */
    @Override
    public void onUpdate(double dt, IInputEvent input) {
        if (go == null) return;
        go.transform().move(new Point(0, speed * dt), 0);

        if (go.engine() != null && go.engine().getBounds() != null) {
            Rectangle bounds = go.engine().getBounds();
            double y = go.transform().position().getY();
            if (y < bounds.getMinY()) { // Assume que minY é o topo do ecrã
                go.engine().destroy(go);
            }
        }
    }


    /**
     * Trata colisões do projétil com outros objetos de jogo.
     * Se o projétil colidir com um inimigo (nome começa por "enemy") ou um obstáculo (nome começa por "obstacle"),
     * o projétil é destruído.
     * @param others Uma lista de IGameObjects com os quais o projétil colidiu. Não deve ser nula.
     * @post Se 'go' colidir com um objeto cujo nome comece por "enemy" ou "obstacle", 'go' é destruído através do seu motor de jogo. A interação para após a primeira colisão relevante.
     */
    @Override
    public void onCollision(List<IGameObject> others) {
        if (go == null || go.engine() == null) return;

        for (IGameObject other : others) {
            if (other.name().startsWith("enemy") || other.name().startsWith("obstacle")) {
                go.engine().destroy(go);
                break;
            }
        }
    }
}