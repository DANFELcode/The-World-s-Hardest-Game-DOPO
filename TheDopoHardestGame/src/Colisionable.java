package domain;

import java.awt.Rectangle;

public interface Colisionable {
    Rectangle getAreaColision();
    void alColisionar(Jugador j);
}
