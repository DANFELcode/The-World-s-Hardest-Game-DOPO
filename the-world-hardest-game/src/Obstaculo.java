package domain;

import java.awt.Rectangle;

public class Obstaculo implements Colisionable {
    private int x;
    private int y;
    private int ancho;
    private int alto;

    public Obstaculo(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    @Override
    public Rectangle getAreaColision() { return null; }

    @Override
    public void alColisionar(Jugador j) {}

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
