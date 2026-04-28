package domain;

import java.awt.Rectangle;

public class ZonaSeguraIntermedia extends Objetivo {
    public ZonaSeguraIntermedia(int x, int y) {
        super(x, y);
    }

    @Override
    public void alColisionar(Jugador jugador) {}

	@Override
	public Rectangle getAreaColision() {		
		return null;
	}
}
