package gameobject.path;

import gameobject.IGameObject;

/**
 * Interface que define um contrato para estratégias de movimento de inimigos.
 * Permite que diferentes tipos de inimigos sigam caminhos distintos.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Qualquer implementação deve ser capaz de atualizar a posição de um IGameObject.
 */
public interface EnemyPath {
    /**
     * Atualiza a posição e/ou estado de um objeto de jogo (inimigo) com base na lógica do caminho.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param go O objeto de jogo (inimigo) a ser movimentado. Não deve ser nulo e deve ter um transform válido.
     * @post A posição do IGameObject 'go' é atualizada de acordo com a lógica de movimento definida pelo caminho e o 'dt' fornecido.
     */
    void update(double dt, IGameObject go);
}