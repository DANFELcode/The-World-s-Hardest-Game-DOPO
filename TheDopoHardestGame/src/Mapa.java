package domain;

public class Mapa {
    private int ancho;
    private int alto;
    private int[][] tablero;

    public Mapa(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        this.tablero = new int[alto][ancho];
    }

    public boolean esPosicionValida(double x, double y) { return false; }
    public boolean hayPared(double x, double y) { return false; }

    public int getAncho() {	return ancho;}
    public int getAlto() {return alto;}
}