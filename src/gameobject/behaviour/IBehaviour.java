package gameobject.behaviour;

import engine.GameEngine;
import engine.IInputEvent;
import gameobject.IGameObject;

import java.util.List;

/**
 * Interface que define o contrato para os comportamentos dos objetos de jogo.
 * Um comportamento encapsula a lógica de atualização, reação a colisões e ciclo de vida de um GameObject.
 * Inclui métodos para interação polimórfica, evitando o uso excessivo de 'instanceof'.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Um IBehaviour está tipicamente associado a um IGameObject.
 */
public interface IBehaviour {

    /**
     * Devolve o objeto de jogo (IGameObject) ao qual este comportamento está associado.
     * @return O IGameObject associado, ou nulo se não estiver associado.
     */
    IGameObject gameObject();

    /**
     * Associa este comportamento a um objeto de jogo (IGameObject).
     * @param go O IGameObject ao qual este comportamento será associado. Pode ser nulo para desassociar.
     * @post O IGameObject interno é definido como 'go'.
     */
    void gameObject(IGameObject go);

    /**
     * Chamado uma vez quando o comportamento é inicializado, geralmente após ser associado a um GameObject
     * e o GameObject ser adicionado ao motor de jogo (GameEngine).
     * @post O estado inicial do comportamento é configurado.
     */
    void onInit();

    /**
     * Chamado quando o GameObject associado é ativado (enabled) no motor de jogo.
     * @post O comportamento pode iniciar lógicas que dependem de estar ativo no jogo.
     */
    void onEnabled();

    /**
     * Chamado quando o GameObject associado é desativado (disabled) no motor de jogo.
     * @post O comportamento pode pausar ou limpar estados relacionados à sua atividade.
     */
    void onDisabled();

    /**
     * Chamado quando o GameObject associado está prestes a ser destruído e removido do motor de jogo.
     * @post O comportamento pode libertar recursos ou realizar ações finais antes da destruição.
     */
    void onDestroy();

    /**
     * Chamado em cada frame do ciclo de jogo para atualizar o estado e a lógica do comportamento.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param input O estado atual dos inputs do jogo (ex: teclas premidas). Pode ser nulo se não houver inputs.
     * @post O estado do GameObject associado é atualizado com base na lógica do comportamento, 'dt' e 'input'.
     */
    void onUpdate(double dt, IInputEvent input);

    /**
     * Chamado quando o GameObject associado colide com outros GameObjects.
     * @param others Uma lista de IGameObjects com os quais ocorreu uma colisão. A lista não deve ser nula, mas pode estar vazia.
     * @post O comportamento reage à(s) colisão(ões) de acordo com a sua lógica específica.
     */
    void onCollision(List<IGameObject> others);

    /**
     * Vincula o motor de jogo (GameEngine) a este comportamento, se necessário.
     * Chamado após o comportamento ser associado a um GameObject e o GameObject ao motor.
     * A implementação padrão não faz nada.
     * @param engine O GameEngine a ser vinculado. Pode ser nulo.
     * @post Se o comportamento necessitar de uma referência ao motor, esta é armazenada.
     */
    default void linkGameEngine(GameEngine engine) {
        // Implementação padrão não faz nada
    }

    /**
     * Verifica se a entidade controlada por este comportamento acabou de ser congelada neste tick
     * e limpa o indicador (flag).
     * @return Verdadeiro se acabou de ser congelada, falso caso contrário. Padrão é falso.
     */
    default boolean wasJustFrozenAndClearFlag() {
        return false;
    }

    /**
     * Verifica se a entidade controlada por este comportamento está atualmente em estado congelado.
     * @return Verdadeiro se congelada, falso caso contrário. Padrão é falso.
     */
    default boolean isCurrentlyFrozen() {
        return false;
    }

    /**
     * Obtém a contagem atual de projéteis para fins de exibição (HUD).
     * Relevante para PlayerBehaviour.
     * @return Contagem atual de projéteis, ou um valor padrão (ex: 0) se não aplicável.
     */
    default int getDisplayBulletCount() {
        return 0;
    }

    /**
     * Obtém a contagem máxima de projéteis para fins de exibição (HUD).
     * Relevante para PlayerBehaviour.
     * @return Contagem máxima de projéteis, ou um valor padrão (ex: 0) se não aplicável.
     */
    default int getMaxDisplayBullets() {
        return 0;
    }

    /**
     * Reinicia o estado específico do jogador, como a contagem de projéteis.
     * Chamado quando um nível é reiniciado ou o jogador perde uma vida.
     * A implementação padrão não faz nada.
     * @post O estado específico do jogador é redefinido para os seus valores iniciais.
     */
    default void resetPlayerSpecificState() {
        // Implementação padrão não faz nada
    }
}