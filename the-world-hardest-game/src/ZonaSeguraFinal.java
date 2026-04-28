package domain;

import java.awt.Rectangle;

public class ZonaSeguraFinal extends Objetivo {
    public ZonaSeguraFinal(int x, int y) {
        super(x, y);
    }

    @Override
    public void alColisionar(Jugador jugador) {}

	@Override
	public Rectangle getAreaColision() {		
		return null;
	}
}
