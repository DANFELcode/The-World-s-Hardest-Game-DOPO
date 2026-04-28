package domain;

import java.awt.Rectangle;

public class Moneda extends Objetivo {
    public Moneda(int x, int y) {
        super(x, y);
    }

    @Override
    public Rectangle getAreaColision() {return null;}

    @Override
    public void alColisionar(Jugador jugador) {}
}
