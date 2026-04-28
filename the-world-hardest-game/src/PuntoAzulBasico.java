package domain;

import java.awt.Rectangle;

public class PuntoAzulBasico extends Enemigo {
	private boolean horizontal;  // true = mov. horizontal, false = vertical
	private int direccion; 
	
    public PuntoAzulBasico(int x, int y) {
        super(x, y, 1.0, "azul");
    }

    @Override
    public void mover() {}

    @Override
    public Rectangle getAreaColision() {return null;}

    @Override
    public void alColisionar(Jugador j) {}
}
