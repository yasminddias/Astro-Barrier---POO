package gameobject.geometry;

import java.util.Locale;

/**
 * Representa um ponto bidimensional com coordenadas (x, y).
 * Permite operações de translação e rotação em torno de um centro arbitrário.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv As coordenadas x e y representam valores válidos; o ponto tem sempre valores definidos.
 */
public class Point {
    private double x, y;

    /**
     * Constrói um ponto com as coordenadas x e y especificadas.
     * @param x A coordenada x do ponto.
     * @param y A coordenada y do ponto.
     * @post Um novo ponto é criado com as coordenadas fornecidas.
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Devolve a coordenada x do ponto.
     * @return O valor da coordenada x.
     */
    public double getX() {
        return x;
    }

    /**
     * Devolve a coordenada y do ponto.
     * @return O valor da coordenada y.
     */
    public double getY() {
        return y;
    }

    /**
     * Define as coordenadas do ponto.
     * @param x A nova coordenada x.
     * @param y A nova coordenada y.
     * @post As coordenadas do ponto são atualizadas para x e y.
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Define as coordenadas deste ponto com base nas coordenadas de outro ponto.
     * @param p O ponto cujas coordenadas serão copiadas. Não deve ser nulo.
     * @post As coordenadas x e y deste ponto são definidas para as coordenadas x e y do ponto p.
     */
    public void set(Point p) {
        if (p != null) {
            this.x = p.getX();
            this.y = p.getY();
        }
    }


    /**
     * Desloca o ponto pelas quantidades dx e dy.
     * @param dx A quantidade a adicionar à coordenada x.
     * @param dy A quantidade a adicionar à coordenada y.
     * @post As coordenadas do ponto são atualizadas: x = x + dx, y = y + dy.
     */
    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * Rotaciona este ponto em torno de um ponto central (centroide) por um determinado ângulo.
     * @param angleDegrees O ângulo de rotação em graus.
     * @param centroid O ponto central em torno do qual a rotação é efetuada. Não deve ser nulo.
     * @return Um novo Ponto que representa este ponto após a rotação.
     * @see Math#toRadians(double)
     */
    public Point rotate(double angleDegrees, Point centroid) {
        double rads = Math.toRadians(angleDegrees);
        double xRel = x - centroid.x;
        double yRel = y - centroid.y;
        double xNew = xRel * Math.cos(rads) - yRel * Math.sin(rads);
        double yNew = xRel * Math.sin(rads) + yRel * Math.cos(rads);
        return new Point(xNew + centroid.x, yNew + centroid.y);
    }

    /**
     * Devolve uma representação textual do ponto, formatada com duas casas decimais.
     * @return Uma string no formato "(x.xx, y.yy)".
     */
    @Override
    public String toString() {
        return String.format(Locale.US, "(%.2f,%.2f)", x, y);
    }
}