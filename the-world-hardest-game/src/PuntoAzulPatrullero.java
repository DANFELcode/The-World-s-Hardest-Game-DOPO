package domain;

import java.awt.Rectangle;
import java.util.List;

public class PuntoAzulPatrullero extends Enemigo {
	private List<int[]> puntoRuta;
	private int indiceActual;
	
    public PuntoAzulPatrullero(int x, int y) {
        super(x, y, 1.0, "azul");
    }

    @Override
    public void mover(Mapa mapa) {}

}
