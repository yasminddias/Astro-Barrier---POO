package gameobject.collider;

import gameobject.geometry.Point;
import gameobject.transform.ITransform;

import java.util.List;

/**
 * Implementa um colisor de forma circular.
 * Responsável por detetar colisões entre este círculo e outros colisores (círculos ou polígonos).
 * O seu estado (centro e raio) é sincronizado com uma ITransform associada.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv center nunca é nulo.
 * @inv originalRadius é sempre não negativo.
 * @inv radius é sempre não negativo e reflete originalRadius * transform.scale().
 * @inv transform (a referência à ITransform) pode ser nula, mas nesse caso o colisor pode não funcionar como esperado sem uma Transform para sincronizar.
 */
public class CircleCollider implements ICollider {
    private Point center;
    private final double originalRadius;
    private double radius;
    private final ITransform transform;

    /**
     * Constrói um CircleCollider.
     * @param x A coordenada x inicial do centro do círculo.
     * @param y A coordenada y inicial do centro do círculo.
     * @param radius O raio original do círculo. Deve ser não negativo.
     * @param transform A transformação (ITransform) à qual este colisor está associado. Usada para sincronizar posição e escala. Pode ser nula, mas idealmente não deveria ser.
     * @post O centro é inicializado em (x,y). O raio original e o raio atual são definidos.
     * @post Se 'transform' não for nula, o colisor é ajustado inicialmente à transformação.
     */
    public CircleCollider(double x, double y, double radius, ITransform transform) {
        this.center = new Point(x, y);
        this.originalRadius = radius;
        this.radius = radius;
        this.transform = transform;
        if (this.transform != null) {
            adjustToTransform();
        }
    }

    /**
     * Ajusta o centro e o raio do colisor com base na transformação associada.
     * Chamado durante a inicialização. A sincronização contínua ocorre em onUpdate.
     * @post O centro do colisor é definido para a posição da transformação.
     * @post O raio do colisor é definido como o raio original multiplicado pela escala da transformação.
     */
    private void adjustToTransform() {
        this.center.set(transform.position());
        this.radius = originalRadius * transform.scale();
    }

    /**
     * Devolve o ponto central atual do colisor.
     * @return O ponto central (Point) do círculo. Nunca é nulo.
     */
    @Override
    public Point centroid() {
        return center;
    }

    /**
     * Move o centro do colisor pelo vetor de deslocamento fornecido.
     * (Nota: O movimento principal deve ser gerido via onUpdate sincronizando com a Transform.)
     * @param dPos O vetor de deslocamento (dx, dy). Não deve ser nulo.
     * @post O centro do círculo é transladado por dPos.
     */
    @Override
    public void move(Point dPos) {
        center.translate(dPos.getX(), dPos.getY());
    }

    /**
     * Rotaciona o colisor. Para um círculo, a rotação do seu próprio eixo não altera a sua forma ou deteção de colisão.
     * A rotação em torno de um ponto externo (GameObject) é tratada pela atualização da sua posição via Transform.
     * @param dTheta O ângulo de rotação em graus (não utilizado).
     * @post Nenhuma alteração no estado do CircleCollider, pois a rotação não afeta a sua forma.
     */
    @Override
    public void rotate(double dTheta) {
        // Não afeta círculos
    }

    /**
     * Escala o raio do colisor por um fator de escala incremental.
     * (Nota: A escala principal deve ser gerida via onUpdate sincronizando com a Transform.)
     * @param dScale O fator de escala a ser aplicado ao raio atual (ex: dScale = 0.1 aumenta o raio em 10%).
     * @post O raio do círculo é multiplicado por (1 + dScale).
     */
    @Override
    public void scale(double dScale) {
        this.radius *= (1 + dScale);
    }

    /**
     * Atualiza o estado do CircleCollider (centro e raio) para corresponder à sua Transform associada.
     * Este método deve ser chamado em cada passo do ciclo de jogo.
     * @post O 'center' do colisor é definido para a 'position' da 'transform'.
     * @post O 'radius' do colisor é definido como 'originalRadius' * 'transform.scale()'.
     * Se 'transform' for nula, não ocorre nenhuma atualização.
     */
    @Override
    public void onUpdate() {
        if (transform != null) {
            this.center.set(transform.position());
            this.radius = this.originalRadius * transform.scale();
        }
    }


    /**
     * Verifica se este colisor está a colidir com outro ICollider.
     * Utiliza double dispatch chamando o método de colisão específico do 'other'.
     * @param other O outro ICollider. Não deve ser nulo.
     * @return Verdadeiro se houver colisão, falso caso contrário.
     */
    @Override
    public boolean isColliding(ICollider other) {
        return other.isColliding(this);
    }

    /**
     * Verifica se este CircleCollider colide com outro CircleCollider.
     * A colisão ocorre se a distância entre os centros for menor que a soma dos raios.
     * @param other O outro CircleCollider. Não deve ser nulo.
     * @return Verdadeiro se os círculos colidirem, falso caso contrário.
     */
    @Override
    public boolean isColliding(CircleCollider other) {
        double dx = center.getX() - other.center.getX();
        double dy = center.getY() - other.center.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (radius + other.radius - 1e-9); // 1e-9 para tolerância a erros de ponto flutuante
    }


    /**
     * Verifica se este CircleCollider colide com um PolygonCollider.
     * A deteção envolve verificar a distância do centro do círculo a cada aresta do polígono
     * e se o centro do círculo está dentro do polígono.
     * @param poly O PolygonCollider. Não deve ser nulo.
     * @return Verdadeiro se o círculo e o polígono colidirem, falso caso contrário.
     */
    @Override
    public boolean isColliding(PolygonCollider poly) {
        List<Point> vertices = poly.getVertices(); // Assume que getVertices() devolve os vértices transformados
        if (vertices == null || vertices.isEmpty()) return false;
        int n = vertices.size();

        // 1. Verificar distância do centro do círculo a cada aresta do polígono
        for (int i = 0; i < n; i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % n); // Próximo vértice, com wrap around
            if (distanceToSegment(p1, p2) < radius - 1e-9) {
                return true;
            }
        }

        // 2. Verificar se o centro do círculo está dentro do polígono
        // (necessário se o polígono for menor que o círculo e estiver completamente dentro dele)
        return pointInPolygon(center, vertices);
    }

    /**
     * Calcula a menor distância do centro deste círculo a um segmento de reta definido por p1 e p2.
     * @param p1 O primeiro ponto do segmento de reta. Não deve ser nulo.
     * @param p2 O segundo ponto do segmento de reta. Não deve ser nulo.
     * @return A distância perpendicular do centro do círculo ao segmento de reta, ou a distância ao ponto final mais próximo se a projeção estiver fora do segmento.
     */
    private double distanceToSegment(Point p1, Point p2) {
        double x0 = center.getX(), y0 = center.getY();
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();

        double dxL = x2 - x1; // Comprimento do segmento em x
        double dyL = y2 - y1; // Comprimento do segmento em y
        double lenSq = dxL * dxL + dyL * dyL; // Quadrado do comprimento do segmento

        if (lenSq < 1e-9) { // Segmento é (quase) um ponto
            return distanceToPoint(p1);
        }

        // Parâmetro t da projeção do centro do círculo na linha que contém o segmento
        // t = [(P - A) . (B - A)] / |B - A|^2
        double t = ((x0 - x1) * dxL + (y0 - y1) * dyL) / lenSq;

        double projX, projY; // Ponto mais próximo no segmento (ou na sua extensão linear)

        if (t < 0) { // Projeção antes de p1
            projX = x1;
            projY = y1;
        } else if (t > 1) { // Projeção depois de p2
            projX = x2;
            projY = y2;
        } else { // Projeção está no segmento
            projX = x1 + t * dxL;
            projY = y1 + t * dyL;
        }

        // Distância do centro do círculo ao ponto mais próximo no segmento
        double distToProjX = x0 - projX;
        double distToProjY = y0 - projY;
        return Math.sqrt(distToProjX * distToProjX + distToProjY * distToProjY);
    }

    /**
     * Verifica se um ponto está dentro de um polígono usando o algoritmo de ray casting (even-odd rule).
     * @param p O ponto a ser verificado. Não deve ser nulo.
     * @param vertices A lista de vértices do polígono, em ordem. Não deve ser nula ou vazia.
     * @return Verdadeiro se o ponto estiver dentro do polígono, falso caso contrário.
     */
    private boolean pointInPolygon(Point p, List<Point> vertices) {
        int n = vertices.size();
        if (n < 3) return false; // Um polígono precisa de pelo menos 3 vértices

        boolean inside = false;
        double px = p.getX();
        double py = p.getY();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double vix = vertices.get(i).getX();
            double viy = vertices.get(i).getY();
            double vjx = vertices.get(j).getX();
            double vjy = vertices.get(j).getY();

            if (((viy > py) != (vjy > py)) &&
                    (px < (vjx - vix) * (py - viy) / (vjy - viy) + vix)) {
                inside = !inside;
            }
        }
        return inside;
    }

    /**
     * Calcula a distância do centro deste círculo a outro ponto.
     * @param p O ponto ao qual calcular a distância. Não deve ser nulo.
     * @return A distância euclidiana entre o centro do círculo e o ponto p.
     */
    private double distanceToPoint(Point p) {
        double dx = center.getX() - p.getX();
        double dy = center.getY() - p.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Devolve uma lista contendo o centro do círculo.
     * Usado por alguns algoritmos de colisão que esperam uma lista de vértices.
     * @return Uma lista contendo um único Ponto: o centro do círculo.
     */
    public List<Point> getVertices() {
        return List.of(center);
    }

    /**
     * Devolve o raio atual do círculo.
     * @return O raio do círculo.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Devolve o raio como a dimensão característica do círculo.
     * @return O raio atual do círculo.
     */
    @Override
    public double getCharacteristicDimension() {
        return radius;
    }
}