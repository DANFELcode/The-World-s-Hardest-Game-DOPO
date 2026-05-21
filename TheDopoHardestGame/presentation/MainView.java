package presentation;

/**
 * Navigation/control contract that the panels use to talk back to the main
 * window without depending on its concrete class. The main window
 * ({@code TheDOPOHardestGameGUI}) implements it and acts as a mediator.
 */
public interface MainView {

    /** Stops the game loop. */
    void detenerLoop();

    /** Switches the displayed view back to the start panel. */
    void mostrarInicio();

    /** Switches the displayed view to the explanation panel. */
    void mostrarExplicacion();

    /** Switches the displayed view to the selection panel. */
    void mostrarSeleccion();

    /** Switches to the game view, gives the board focus and starts the loop. */
    void iniciarJuego();

    /** Notifies that the pause state changed, so the menu label can update. */
    void onPausaCambiada();
}
