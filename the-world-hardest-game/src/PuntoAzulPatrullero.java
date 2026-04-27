package domain;

import java.awt.Rectangle;

public class PuntoAzulPatrullero extends Enemigo {
    public PuntoAzulPatrullero(int x, int y) {
        super(x, y, 1.0, "azul");
    }

    @Override
    public void mover() {}

    @Override
    public Rectangle getAreaColision() {return null;}

    @Override
    public void alColisionar(Jugador j) {}
}