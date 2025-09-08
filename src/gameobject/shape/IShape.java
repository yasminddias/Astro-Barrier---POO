package gameobject.shape;

import java.awt.*;

/**
 * Interface que define o contrato para formas de objetos de jogo que podem ser renderizadas.
 * Responsável por especificar como um objeto deve ser desenhado num contexto gráfico.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv Qualquer implementação deve ser capaz de se renderizar num Graphics2D.
 */
public interface IShape {
    /**
     * Renderiza a forma no contexto gráfico fornecido.
     * @param g2 O contexto Graphics2D onde a forma será desenhada. Não deve ser nulo.
     * @param x A coordenada x do centro (ou ponto de referência) da forma no ecrã.
     * @param y A coordenada y do centro (ou ponto de referência) da forma no ecrã.
     * @param angle O ângulo de rotação da forma em graus.
     * @param scale O fator de escala da forma. Deve ser positivo.
     * @param layer A camada de renderização (pode ser usada para z-ordering, embora a renderização primária seja controlada pelo GameEngine).
     * @post A forma é desenhada em g2 de acordo com os parâmetros fornecidos.
     */
    public void render(Graphics2D g2, int x, int y, double angle, double scale, int layer);
}