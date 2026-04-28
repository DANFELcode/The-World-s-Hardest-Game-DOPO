package domain;

public class JugadorVerde extends Jugador {
    private boolean absorbioPrimerGolpe;

    public JugadorVerde(String nombre) {
    	super(nombre, 1.0, "verde");
        this.absorbioPrimerGolpe = false;
    }
    
    public void moverJugador(char dir) {

    }
    
    @Override
    public void morir() {}
}
