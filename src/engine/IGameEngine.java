package engine;

import gameobject.IGameObject;
import java.util.List; // Necessário para List

/**
 * Interface que define o contrato para o motor de jogo (GameEngine).
 * O motor é responsável por gerir o ciclo de vida dos objetos de jogo (IGameObject),
 * executar o ciclo principal do jogo (game loop), atualizar os comportamentos dos objetos,
 * e detetar colisões.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Listas de objetos ativos e inativos são mantidas pelo motor.
 */
public interface IGameEngine {
    /**
     * Adiciona um objeto de jogo à lista de objetos ativos (enabled) no motor.
     * O objeto será atualizado e renderizado em cada ciclo do jogo.
     * @param go O IGameObject a ser adicionado. Não deve ser nulo.
     * @post O 'go' é adicionado à lista de objetos ativos, e os seus métodos onInit() e onEnabled() são chamados.
     */
    void addEnabled(IGameObject go);

    /**
     * Adiciona um objeto de jogo à lista de objetos inativos (disabled) no motor.
     * O objeto não será atualizado nem renderizado até ser ativado.
     * @param go O IGameObject a ser adicionado. Não deve ser nulo.
     * @post O 'go' é adicionado à lista de objetos inativos, e o seu método onInit() (e possivelmente onDisabled()) é chamado.
     */
    void addDisabled(IGameObject go);

    /**
     * Ativa um objeto de jogo que estava previamente desativado.
     * Move o objeto da lista de inativos para a lista de ativos.
     * @param go O IGameObject a ser ativado. Não deve ser nulo.
     * @post Se 'go' estava na lista de inativos, é movido para a lista de ativos e o seu método onEnabled() é chamado.
     */
    void enable(IGameObject go);

    /**
     * Desativa um objeto de jogo que estava previamente ativo.
     * Move o objeto da lista de ativos para a lista de inativos.
     * @param go O IGameObject a ser desativado. Não deve ser nulo.
     * @post Se 'go' estava na lista de ativos, é movido para a lista de inativos e o seu método onDisabled() é chamado.
     */
    void disable(IGameObject go);

    /**
     * Verifica se um objeto de jogo está atualmente na lista de objetos ativos.
     * @param go O IGameObject a ser verificado. Não deve ser nulo.
     * @return Verdadeiro se 'go' estiver ativo, falso caso contrário.
     */
    boolean isEnabled(IGameObject go);

    /**
     * Verifica se um objeto de jogo está atualmente na lista de objetos inativos.
     * @param go O IGameObject a ser verificado. Não deve ser nulo.
     * @return Verdadeiro se 'go' estiver inativo, falso caso contrário.
     */
    boolean isDisabled(IGameObject go);

    /**
     * Devolve uma lista de todos os objetos de jogo atualmente ativos no motor.
     * @return Uma lista contendo os IGameObjects ativos. A lista pode ser uma cópia para evitar modificações concorrentes.
     */
    List<IGameObject> getEnabled();

    /**
     * Devolve uma lista de todos os objetos de jogo atualmente inativos no motor.
     * @return Uma lista contendo os IGameObjects inativos. A lista pode ser uma cópia.
     */
    List<IGameObject> getDisabled();

    /**
     * Remove um objeto de jogo do motor, independentemente de estar ativo ou inativo.
     * O método onDestroy() do comportamento do objeto é chamado.
     * @param go O IGameObject a ser destruído. Não deve ser nulo.
     * @post 'go' é removido das listas de objetos ativos e inativos. O método onDestroy() do seu comportamento é chamado.
     */
    void destroy(IGameObject go);

    /**
     * Remove todos os objetos de jogo (ativos e inativos) do motor.
     * O método onDestroy() do comportamento de cada objeto é chamado.
     * @post Todas as listas de objetos (ativos e inativos) ficam vazias. onDestroy() é chamado para todos os objetos removidos.
     */
    void destroyAll();

    /**
     * Executa um único passo do ciclo de jogo (game loop).
     * Atualiza todos os objetos de jogo ativos, processa inputs e verifica colisões.
     * @param dt O tempo decorrido desde o último passo do ciclo (delta time), em segundos. Deve ser não negativo.
     * @param input O estado atual dos inputs do jogo. Pode ser nulo se não houver inputs a processar.
     * @post Todos os IGameObjects ativos são atualizados (comportamento, colisor).
     * @post As colisões entre objetos ativos são verificadas e tratadas.
     * @post Os objetos podem ser restringidos aos limites (bounds) do motor.
     */
    void run(double dt, IInputEvent input);

    /**
     * Verifica e processa colisões entre todos os objetos de jogo ativos.
     * Quando uma colisão é detetada, o método onCollision() dos comportamentos dos objetos envolvidos é chamado.
     * @post Os métodos onCollision() dos IBehaviours dos objetos que colidiram são invocados.
     */
    void checkCollisions();
}