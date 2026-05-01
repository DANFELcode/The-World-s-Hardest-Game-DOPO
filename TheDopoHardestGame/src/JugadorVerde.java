package domain;

/**
 * Representa el jugador resistente del juego. <br>
 * <b>(nombre, velocidad=1.0, color="verde", absorbioPrimerGolpe)</b> <br>
 * <b>Inv:</b> velocidad > 0
 */
public class JugadorVerde extends Jugador {
    private boolean absorbioPrimerGolpe;

    /**
     * Crea el jugador verde con escudo activo al inicio.
     * @param nombre nombre del jugador
     */
    public JugadorVerde(String nombre) {
    	super(nombre, 1.0, "verde");
        this.absorbioPrimerGolpe = false;
    }

    /**
     * Al primer contacto con un enemigo absorbe el golpe y reduce la velocidad.
     * Al segundo contacto muere, suma una muerte y respawnea.
     */
    @Override
    public void morir() {
        if (!absorbioPrimerGolpe) {
            absorbioPrimerGolpe = true;
            setVelocidad(getVelocidad() * 0.7);
        } else {
            super.morir();
        }
    }

    /**
     * Recupera el escudo y restaura la velocidad original al iniciar un nuevo nivel.
     */
    // recupera su habilidad
    public void recuperarEscudo() {
        absorbioPrimerGolpe = false;
        setVelocidad(1.0);
    }
}