package domain;

public class JugadorRojo extends Jugador {
    public JugadorRojo(String nombre) {
        super(nombre);
        setVelocidad(1.0);
        setColor("rojo");
    }
    
    //metodos de control de movimiento
    public void moverJugador(char dir) {

    }
}
