package domain;

public class PlayerVsPlayer extends ModoDeJuego {
    public PlayerVsPlayer() {
        super();
    }

    @Override
    public void inicializarJugadores() {}

    @Override
    public boolean verificarVictoria() { return false; }
}
