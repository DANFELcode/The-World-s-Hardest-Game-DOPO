package domain;

import java.awt.Rectangle;

public abstract class Enemigo extends ElementoMovil implements Colisionable{
    public Enemigo(int x, int y, double velocidad, String color) {
        super(x, y, velocidad, color);
    }
    
    public abstract void mover();
    
    @Override
    public abstract Rectangle getAreaColision();

    @Override
    public abstract void alColisionar(Jugador j);
}
