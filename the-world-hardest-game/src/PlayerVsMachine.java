package domain;

public class PlayerVsMachine extends ModoDeJuego {
    public PlayerVsMachine() {
        super();
    }

    @Override
    public void inicializarJugadores() {}

    @Override
    public boolean verificarVictoria() { return false; }
}
