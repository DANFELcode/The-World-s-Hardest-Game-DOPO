package domain;

import java.awt.Rectangle;

public class PuntoAzulRapido extends Enemigo {
    public PuntoAzulRapido(int x, int y) {
        super(x, y, 2.0, "azul");
    }

    @Override
    public void mover(Mapa mapa) {}
}
