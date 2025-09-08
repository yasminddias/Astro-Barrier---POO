package gameobject.collider;

import gameobject.geometry.Point;
import gameobject.transform.ITransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa um colisor de forma poligonal.
 * Responsável por detetar colisões usando o Teorema do Eixo Separador (SAT) para colisões polígono-polígono,
 * e lógicas específicas para polígono-círculo.
 * Os seus vértices são definidos localmente e transformados para o espaço do mundo através de uma ITransform associada.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv vertices (locais) nunca é nulo e contém os vértices originais do polígono.
 * @inv transformedVertices nunca é nulo e contém os vértices transformados para o espaço do mundo; o seu tamanho corresponde a 'vertices'.
 * @inv transform (a referência à ITransform) pode ser nula, mas nesse caso o colisor não funcionará corretamente sem uma Transform para sincronizar.
 */
public class PolygonCollider implements ICollider {
    private List<Point> vertices;
    private List<Point> transformedVertices;
    private final ITransform transform;

    /**
     * Constrói um PolygonCollider a partir de um array de coordenadas de vértices e uma transformação.
     * As coordenadas são pares (x,y) definindo os vértices do polígono em ordem.
     * @param coords Um array de doubles representando os vértices do polígono no formato [x1, y1, x2, y2, ...]. Deve ter um número par de elementos e pelo menos 6 (para um triângulo).
     * @param transform A transformação (ITransform) à qual este colisor está associado. Usada para converter vértices locais para o espaço do mundo.
     * @post Os 'vertices' locais são inicializados a partir de 'coords'.
     * @post 'transformedVertices' é inicializado com o mesmo tamanho que 'vertices'.
     * @post O método onUpdate() é chamado para calcular a posição inicial dos 'transformedVertices'.
     */
    public PolygonCollider(double[] coords, ITransform transform) {
        this.vertices = new ArrayList<>();
        this.transformedVertices = new ArrayList<>();
        this.transform = transform;

        if (coords.length % 2 != 0) {
            // Lançar exceção ou tratar erro, pois as coordenadas devem vir em pares
            System.err.println("PolygonCollider: Array de coordenadas deve ter um número par de elementos.");
            // Inicializar com um estado seguro, por exemplo, um ponto ou vazio, para evitar NullPointerExceptions
            // No entanto, para este exercício, assumimos que os dados de entrada são válidos conforme o uso no projeto.
        }

        for (int i = 0; i < coords.length; i += 2) {
            vertices.add(new Point(coords[i], coords[i + 1]));
            transformedVertices.add(new Point(0,0)); // Preencher com pontos dummy para inicializar a lista
        }
        onUpdate(); // Calcula as posições iniciais dos vértices transformados
    }


    /**
     * Calcula e devolve o centroide dos vértices transformados do polígono.
     * @return O ponto central (Point) do polígono no espaço do mundo. Devolve (0,0) ou a posição da transform se não houver vértices.
     */
    @Override
    public Point centroid() {
        if (transformedVertices == null || transformedVertices.isEmpty()) {
            return (transform != null) ? transform.position() : new Point(0,0);
        }
        double xSum = 0, ySum = 0, signedAreaTimesTwo = 0;
        int n = transformedVertices.size();

        for (int i = 0; i < n; i++) {
            Point p1 = transformedVertices.get(i);
            Point p2 = transformedVertices.get((i + 1) % n);

            double crossProductTerm = (p1.getX() * p2.getY()) - (p2.getX() * p1.getY());
            signedAreaTimesTwo += crossProductTerm;
            xSum += (p1.getX() + p2.getX()) * crossProductTerm;
            ySum += (p1.getY() + p2.getY()) * crossProductTerm;
        }

        if (Math.abs(signedAreaTimesTwo) < 1e-9) { // Polígono degenerado (área zero)
            if (n > 0) { // Calcula a média dos vértices como fallback
                double avgX = 0, avgY = 0;
                for(Point p : transformedVertices) { avgX += p.getX(); avgY += p.getY(); }
                return new Point(avgX/n, avgY/n);
            }
            return (transform != null) ? transform.position() : new Point(0,0);
        }
        return new Point(xSum / (3.0 * signedAreaTimesTwo), ySum / (3.0 * signedAreaTimesTwo));
    }

    /**
     * Move o colisor. (Nota: Geralmente gerido por onUpdate sincronizando com a Transform.)
     * @param dPos Vetor de deslocamento. Não deve ser nulo.
     * @post Os 'transformedVertices' são transladados por dPos.
     */
    @Override
    public void move(Point dPos) {
        if (transformedVertices != null) {
            for (Point p : transformedVertices) {
                p.translate(dPos.getX(), dPos.getY());
            }
        }
    }

    /**
     * Rotaciona o colisor. (Nota: Geralmente gerido por onUpdate.)
     * @param dTheta Ângulo de rotação em graus.
     * @post Nenhuma alteração direta; a rotação é aplicada em onUpdate através da transform.
     */
    @Override
    public void rotate(double dTheta) {
        // A rotação é tratada por onUpdate() através da transformação.
    }

    /**
     * Escala o colisor. (Nota: Geralmente gerido por onUpdate.)
     * @param dScale Fator de escala.
     * @post Nenhuma alteração direta; a escala é aplicada em onUpdate através da transform.
     */
    @Override
    public void scale(double dScale) {
        // A escala é tratada por onUpdate() através da transformação.
    }

    /**
     * Atualiza os 'transformedVertices' do polígono com base na 'transform' associada.
     * Aplica escala, rotação e translação aos vértices locais originais.
     * @post Os 'transformedVertices' são recalculados para refletir o estado atual da 'transform'.
     * Se 'transform' ou 'vertices' for nulo, ou se 'transformedVertices' não tiver o mesmo tamanho que 'vertices', não ocorre atualização.
     */
    @Override
    public void onUpdate() {
        if (transform == null || vertices == null || transformedVertices == null || vertices.size() != transformedVertices.size()) return;

        Point transformPos = transform.position();
        double angleRad = Math.toRadians(transform.angle());
        double currentScale = transform.scale();
        double cosA = Math.cos(angleRad);
        double sinA = Math.sin(angleRad);

        for (int i = 0; i < vertices.size(); i++) {
            Point localP = vertices.get(i);
            Point worldP = transformedVertices.get(i);

            double scaledX = localP.getX() * currentScale;
            double scaledY = localP.getY() * currentScale;

            double rotatedX = scaledX * cosA - scaledY * sinA;
            double rotatedY = scaledX * sinA + scaledY * cosA;

            worldP.set(rotatedX + transformPos.getX(), rotatedY + transformPos.getY());
        }
    }

    /**
     * Verifica colisão com outro ICollider usando double dispatch.
     * @param other O outro colisor. Não deve ser nulo.
     * @return Verdadeiro se colidirem, falso caso contrário.
     */
    @Override
    public boolean isColliding(ICollider other) {
        return other.isColliding(this);
    }

    /**
     * Verifica colisão com um CircleCollider. Deferido para a implementação em CircleCollider.
     * @param other O CircleCollider. Não deve ser nulo.
     * @return Verdadeiro se colidirem, falso caso contrário.
     */
    @Override
    public boolean isColliding(CircleCollider other) {
        return other.isColliding(this);
    }

    /**
     * Verifica colisão com outro PolygonCollider usando o Teorema do Eixo Separador (SAT).
     * @param other O outro PolygonCollider. Não deve ser nulo.
     * @return Verdadeiro se os polígonos colidirem, falso caso contrário.
     */
    @Override
    public boolean isColliding(PolygonCollider other) {
        List<Point> axes1 = getAxes(this.transformedVertices);
        if (axes1.isEmpty()) return false;
        List<Point> axes2 = getAxes(other.transformedVertices);
        if (axes2.isEmpty()) return false;

        for (Point axis : axes1) {
            Projection p1 = project(this.transformedVertices, axis);
            Projection p2 = project(other.transformedVertices, axis);
            if (!p1.overlaps(p2)) {
                return false;
            }
        }

        for (Point axis : axes2) {
            Projection p1 = project(this.transformedVertices, axis);
            Projection p2 = project(other.transformedVertices, axis);
            if (!p1.overlaps(p2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtém os eixos de projeção (normais às arestas) para um conjunto de vértices.
     * @param verticesDoPoligono Lista de vértices do polígono. Pode ser nula ou vazia.
     * @return Uma lista de Points, cada um representando um eixo normalizado. Lista vazia se 'verticesDoPoligono' for nulo/vazio ou tiver menos de 2 vértices.
     */
    private List<Point> getAxes(List<Point> verticesDoPoligono) {
        List<Point> axes = new ArrayList<>();
        if (verticesDoPoligono == null || verticesDoPoligono.size() < 2) return axes;

        for (int i = 0; i < verticesDoPoligono.size(); i++) {
            Point p1 = verticesDoPoligono.get(i);
            Point p2 = verticesDoPoligono.get((i + 1) % verticesDoPoligono.size());
            Point edge = new Point(p2.getX() - p1.getX(), p2.getY() - p1.getY());
            Point normal = new Point(-edge.getY(), edge.getX()); // Perpendicular

            // Normalizar o eixo (opcional para SAT básico, mas boa prática)
            double length = Math.sqrt(normal.getX() * normal.getX() + normal.getY() * normal.getY());
            if (length > 1e-9) { // Evitar divisão por zero
                axes.add(new Point(normal.getX() / length, normal.getY() / length));
            } else {
                // Se a aresta tiver comprimento zero, não podemos obter uma normal significativa desta forma.
                // Poderia adicionar um eixo arbitrário ou ignorar, dependendo da robustez necessária.
                // Para este caso, ignoramos eixos de arestas degeneradas.
            }
        }
        return axes;
    }

    /**
     * Projeta os vértices de um polígono num eixo.
     * @param verticesDoPoligono Lista de vértices. Pode ser nula ou vazia.
     * @param axis O eixo de projeção. Não deve ser nulo.
     * @return Um objeto Projection contendo os valores mínimo e máximo da projeção. Se 'verticesDoPoligono' for nulo/vazio, devolve uma projeção de [Infinito, -Infinito].
     */
    private Projection project(List<Point> verticesDoPoligono, Point axis) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        if (verticesDoPoligono != null) {
            for (Point vertex : verticesDoPoligono) {
                double dotProduct = vertex.getX() * axis.getX() + vertex.getY() * axis.getY();
                min = Math.min(min, dotProduct);
                max = Math.max(max, dotProduct);
            }
        }
        return new Projection(min, max);
    }

    /**
     * Classe auxiliar para representar a projeção de uma forma num eixo (um intervalo 1D).
     */
    private static class Projection {
        double min, max;
        /**
         * Constrói uma Projeção.
         * @param min O valor mínimo da projeção.
         * @param max O valor máximo da projeção.
         */
        public Projection(double min, double max) {
            this.min = min;
            this.max = max;
        }
        /**
         * Verifica se esta projeção se sobrepõe a outra.
         * @param other A outra projeção. Não deve ser nula.
         * @return Verdadeiro se houver sobreposição, falso caso contrário.
         */
        public boolean overlaps(Projection other) {
            return this.max >= other.min && other.max >= this.min;
        }
    }

    /**
     * Devolve a lista de vértices transformados (no espaço do mundo).
     * @return Uma lista de Points representando os vértices do polígono no espaço do mundo. Pode ser uma lista vazia se não houver vértices.
     */
    public List<Point> getVertices() {
        return transformedVertices;
    }

    /**
     * Devolve uma dimensão característica do polígono.
     * Calcula a largura da bounding box alinhada aos eixos dos vértices transformados e devolve metade dessa largura.
     * @return Metade da largura da bounding box do polígono, ou 0 se não houver vértices.
     */
    @Override
    public double getCharacteristicDimension() {
        if (transformedVertices == null || transformedVertices.isEmpty()) {
            return 0;
        }
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;

        for (Point p : transformedVertices) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
        }
        return (maxX - minX) / 2.0;
    }
}