package engine;

/**
 * Interface que define um contrato para representar o estado dos inputs do jogo.
 * Atualmente, foca-se em verificar se uma tecla específica está premida.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Qualquer implementação deve ser capaz de reportar o estado de pressão de uma tecla.
 */
public interface IInputEvent {
    /**
     * Verifica se uma tecla, identificada pelo seu código, está atualmente premida.
     * @param keyCode O código da tecla (ex: KeyEvent.VK_SPACE, KeyEvent.VK_LEFT).
     * Os valores de keyCode são geralmente definidos na classe java.awt.event.KeyEvent.
     * @return Verdadeiro se a tecla estiver premida, falso caso contrário.
     */
    boolean isKeyPressed(int keyCode);
}