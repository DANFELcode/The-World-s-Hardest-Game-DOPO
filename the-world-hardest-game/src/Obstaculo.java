package domain;

import java.awt.Rectangle;

public class Obstaculo implements Colisionable {
    private double x;
    private double y;
    private double ancho;
    private double alto;

    public Obstaculo(double x, double y, double ancho, double alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    @Override
    public Rectangle getAreaColision() { return null; }

    @Override
    public void alColisionar(Jugador j) {}

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAncho() { return ancho; }
    public double getAlto() { return alto; }
}
