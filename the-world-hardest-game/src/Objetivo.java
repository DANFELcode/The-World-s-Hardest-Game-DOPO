package domain;

public abstract class Objetivo implements Colisionable {
    private double x;
    private double y;
    private boolean obtenido;

    public Objetivo(double x, double y) {
    	this.x = x;
    	this.y = y;
    	this.obtenido = false;
    }

    public boolean isObtenido() {
    	return obtenido;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
