package presentation;

import javax.swing.Timer;

/**
 * Drives the game ticks at a fixed FPS, calling update and refresh on the GUI on each tick. <br>
 * <b>(timer, gui, FPS, DELAY)</b> <br>
 * <b>Inv:</b> FPS > 0 and gui != null
 */
public class GameLoop {

    private Timer timer;
    private TheDOPOHardestGameGUI gui;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    /**
     * Creates a game loop bound to the given GUI.
     * @param gui the GUI to update on each tick
     */
    public GameLoop(TheDOPOHardestGameGUI gui) {
        this.gui = gui;
        timer = new Timer(DELAY, e -> tick());
    }

    /**
     * Performs one tick: update game state and refresh the view.
     */
    private void tick() {
        gui.update();
        gui.refresh();
    }

    /**
     * Starts the game loop.
     */
    public void start() { timer.start(); }

    /**
     * Stops the game loop.
     */
    public void stop() { timer.stop(); }
}
