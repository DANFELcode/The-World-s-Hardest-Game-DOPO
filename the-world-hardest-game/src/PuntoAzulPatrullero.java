package domain;

import java.util.List;

/**
 * Representa el enemigo patrullero del juego. <br>
 * <b>(x, y, velocidad=1.0, color="azul", ruta, indiceActual)</b> <br>
 * <b>Inv:</b> ruta != null and ruta.size() > 0 and indiceActual >= 0 and indiceActual < ruta.size()
 */
public class PuntoAzulPatrullero extends Enemigo {
	private List<double[]> ruta;
	private int indiceActual;

	/**
	 * Crea un enemigo patrullero con una ruta de puntos predefinida.
	 * @param x posición horizontal inicial
	 * @param y posición vertical inicial
	 * @param ruta lista de puntos que define el recorrido del patrullero
	 */
	public PuntoAzulPatrullero(double x, double y, List<double[]> ruta) {
	    super(x, y, 1.0, "azul");
	    this.ruta = ruta;
	    this.indiceActual = 0;
	}

	/**
	 * Desplaza al enemigo hacia el siguiente punto de su ruta.
	 * Al llegar al último punto vuelve al primero.
	 * @param mapa mapa del nivel para verificar colisiones con paredes
	 */
	@Override
	public void mover(Mapa mapa) {
	    int indiceSiguiente;
	    if (indiceActual + 1 == ruta.size()) {
	        indiceSiguiente = 0;
	    } else {
	        indiceSiguiente = indiceActual + 1;
	    }

	    double[] siguientePunto = ruta.get(indiceSiguiente);
	    double destinoX = siguientePunto[0];
	    double destinoY = siguientePunto[1];

	    if (!mapa.hayPared(destinoX, destinoY)) {
	        setX(destinoX);
	        setY(destinoY);
	        indiceActual = indiceSiguiente;
	    }
	}
}
