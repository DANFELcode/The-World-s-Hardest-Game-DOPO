package domain;

public abstract class Objetivo implements Colisionable {
    private int x;
    private int y;
    private boolean obtenido;

    public Objetivo(int x, int y) {
    	this.x = x;
    	this.y = y;
    	this.obtenido = false;
    } 

    public boolean isObtenido() {
    	return obtenido;
    }
    
    
    public int getX() {return 0;}
    public int getY() {return 0;}    
}
