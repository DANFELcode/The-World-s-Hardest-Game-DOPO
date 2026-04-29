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

    /**
     * Mueve al jugador una celda en la dirección indicada.
     * @param dir dirección del movimiento: 'N' (norte), 'S' (sur), 'E' (este), 'W' (oeste)
     */
    public void moverJugador(char dir) {
    	switch(dir) {
    	case 'N':
    		double nuevoYN = getY() + getVelocidad();
    		setY(nuevoYN);
    		break;
    	case 'S':
    		double nuevoYS = getY() - getVelocidad();
    		setY(nuevoYS);
    		break;
    	case 'W':
    		double nuevoXW = getX() - getVelocidad();
    		setX(nuevoXW);
    		break;
    	case 'E':
    		double nuevoXE = getX() + getVelocidad();
    		setX(nuevoXE);
    		break;
    	}
    }
}
