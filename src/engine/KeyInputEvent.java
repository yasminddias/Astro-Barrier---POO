package engine;

import java.awt.event.KeyEvent; // Importado para referência aos KeyEvent.VK_ constantes, embora não usado diretamente aqui.
import java.util.HashSet;
import java.util.Set;

/**
 * Implementação de IInputEvent que regista o estado das teclas premidas.
 * Utiliza um conjunto (Set) para armazenar os códigos das teclas que estão atualmente premidas.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv pressedKeys nunca é nulo.
 * @inv pressedKeys contém apenas códigos de teclas inteiros válidos.
 */
public class KeyInputEvent implements IInputEvent {
    private final Set<Integer> pressedKeys = new HashSet<>();
    
    /**
     * Verifica se uma tecla específica está atualmente registada como premida.
     * @param keyCode O código da tecla a verificar.
     * Os valores de keyCode são geralmente definidos na classe java.awt.event.KeyEvent.
     * @return Verdadeiro se a tecla com o keyCode especificado estiver no conjunto de teclas premidas, falso caso contrário.
     */
    @Override
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
}