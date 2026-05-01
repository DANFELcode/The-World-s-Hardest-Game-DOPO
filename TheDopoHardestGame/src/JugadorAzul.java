package domain;

/**
 * Representa el jugador rápido del juego. <br>
 * <b>(nombre, velocidad=1.5, color="azul")</b> <br>
 * <b>Inv:</b> velocidad == 1.5
 */
public class JugadorAzul extends Jugador {

	/**
	 * Crea el jugador azul con velocidad aumentada.
	 * @param nombre nombre del jugador
	 */
	public JugadorAzul(String nombre) {
	    super(nombre, 1.5, "azul");
	}
}