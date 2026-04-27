package domain;

public class Player extends ModoDeJuego {
    public Player() {
        super();
    }

    @Override
    public void inicializarJugadores() {}

    @Override
    public boolean verificarVictoria() { return false; }
}
