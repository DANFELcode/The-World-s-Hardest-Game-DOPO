package domain;

/**
 * Representa el jugador clásico de velocidad y tamaño estándar. <br>
 * <b>(nombre, velocidad=1.0, color="rojo")</b> <br>
 * <b>Inv:</b> velocidad == 1.0
 */
public class JugadorRojo extends Jugador {

    /**
     * Crea el jugador rojo con velocidad estándar.
     * @param nombre nombre del jugador
     */
    public JugadorRojo(String nombre) {
    	super(nombre, 1.0, "rojo");
    }

}
