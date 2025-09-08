package gameobject.behaviour;

import engine.GameEngine;
import engine.IInputEvent;
import gameobject.IGameObject;
import gameobject.path.EnemyPath;
import gameobject.shape.ShapeImage;
import gui.Assets;

import java.util.List;

/**
 * Define o comportamento de um inimigo no jogo.
 * Responsável pelo movimento do inimigo (usando um EnemyPath), deteção de colisão com projéteis do jogador,
 * e alteração do seu estado (ex: congelamento) e aparência.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O IGameObject associado (go) deve existir para que o comportamento funcione.
 * @inv 'path' pode ser nulo se o inimigo for estático, mas tipicamente é um EnemyPath válido.
 * @inv 'isFrozen' reflete o estado de congelamento do inimigo.
 */
public class EnemyBehaviour implements IBehaviour {
    private IGameObject go;
    private boolean stopped = false; // Flag para parar movimento independentemente do congelamento (uso não claro no código atual)
    private final EnemyPath path;
    private boolean isFrozen = false;
    private boolean justFrozenThisTick = false; // Flag para indicar que foi congelado neste tick (para contagem de pontos)
    private GameEngine engine; // Referência ao motor de jogo

    /**
     * Constrói um EnemyBehaviour com um caminho de movimento específico.
     * @param path O EnemyPath que o inimigo seguirá. Pode ser nulo para inimigos estáticos.
     * @post O caminho ('path') do comportamento é inicializado.
     */
    public EnemyBehaviour(EnemyPath path) {
        this.path = path;
    }

    /**
     * Devolve o IGameObject ao qual este comportamento está associado.
     * @return O IGameObject associado.
     */
    @Override
    public IGameObject gameObject() { return go; }

    /**
     * Associa este comportamento a um IGameObject.
     * @param go O IGameObject a associar.
     * @post O campo 'go' é definido para o IGameObject fornecido.
     */
    @Override
    public void gameObject(IGameObject go) { this.go = go; }

    /**
     * Método de inicialização do comportamento. Não realiza ações específicas para EnemyBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override public void onInit() {}
    /**
     * Chamado quando o GameObject associado é ativado. Não realiza ações específicas para EnemyBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override public void onEnabled() {}
    /**
     * Chamado quando o GameObject associado é desativado. Não realiza ações específicas para EnemyBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override public void onDisabled() {}
    /**
     * Chamado quando o GameObject associado é destruído. Não realiza ações específicas para EnemyBehaviour.
     * @post Nenhum estado específico é alterado.
     */
    @Override public void onDestroy() {}

    /**
     * Atualiza o estado do inimigo em cada frame.
     * Se não estiver congelado ou parado, e tiver um caminho e um motor de jogo associado, atualiza a sua posição usando o EnemyPath.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param input O estado atual dos inputs (não utilizado por este comportamento).
     * @post Se aplicável, a posição do IGameObject 'go' é atualizada pelo 'path'.
     */
    @Override
    public void onUpdate(double dt, IInputEvent input) {
        if (isFrozen || stopped || go == null || this.engine == null) return; // Adicionada verificação de this.engine
        if (path != null) { // O motor já está no 'go' se foi corretamente associado
            path.update(dt, go);
        }
    }

    /**
     * Trata colisões do inimigo com outros objetos de jogo.
     * Se o inimigo colidir com um projétil do jogador (nome começa por "player_bullet") e não estiver já congelado,
     * o inimigo é marcado como congelado, o seu movimento é parado, a sua forma visual é alterada para a de inimigo congelado,
     * e a flag 'justFrozenThisTick' é ativada.
     * @param others Uma lista de IGameObjects com os quais o inimigo colidiu. Não deve ser nula.
     * @post Se 'go' colidir com um "player_bullet" e não estiver 'isFrozen':
     * 'isFrozen' torna-se verdadeiro.
     * 'stopped' torna-se verdadeiro.
     * 'justFrozenThisTick' torna-se verdadeiro.
     * A forma (shape) de 'go' é mudada para Assets.FROZEN_ENEMY.
     */
    @Override
    public void onCollision(List<IGameObject> others) {
        if (isFrozen || go == null) return; // Se já está congelado ou não há objeto, não faz nada

        for (IGameObject other : others) {
            if (other.name().startsWith("player_bullet")) {
                if (!isFrozen) {
                    this.justFrozenThisTick = true; // Marcar para pontuação apenas na transição para congelado
                }
                this.isFrozen = true;
                this.stopped = true;

                go.changeShape(new ShapeImage(Assets.FROZEN_ENEMY));
                break; // Um projétil é suficiente para congelar
            }
        }
    }

    /**
     * Vincula o GameEngine a este comportamento.
     * @param engine O GameEngine a ser vinculado.
     * @post A referência interna 'engine' é definida.
     */
    @Override
    public void linkGameEngine(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Para o movimento do inimigo. Define a flag 'stopped' como verdadeira.
     * @post A flag 'stopped' é definida como true.
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * Verifica se o inimigo está atualmente congelado.
     * @return Verdadeiro se o inimigo estiver congelado, falso caso contrário.
     */
    @Override
    public boolean isCurrentlyFrozen() {
        return this.isFrozen;
    }

    /**
     * Verifica se o inimigo foi congelado precisamente neste tick de atualização e limpa a flag.
     * Usado para garantir que a pontuação é atribuída apenas uma vez por congelamento.
     * @return Verdadeiro se o inimigo acabou de ser congelado neste tick, falso caso contrário.
     * @post A flag 'justFrozenThisTick' é definida como false após esta chamada.
     */
    @Override
    public boolean wasJustFrozenAndClearFlag() {
        if (justFrozenThisTick) {
            justFrozenThisTick = false;
            return true;
        }
        return false;
    }
}