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
    private static final double MAX_DELTA = 1.0 / FPS;

    private long lastTime;

    /**
     * Creates a game loop bound to the given GUI.
     * @param gui the GUI to update on each tick
     */
    public GameLoop(TheDOPOHardestGameGUI gui) {
        this.gui = gui;
        timer = new Timer(DELAY, e -> tick());
    }

    /**
     * Performs one tick: computes elapsed time, updates game state, and refreshes the view.
     */
    private void tick() {
        long now = System.nanoTime();
        double deltaTime = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        if (deltaTime > MAX_DELTA) deltaTime = MAX_DELTA;

        gui.update(deltaTime);
        gui.refresh();
    }

    /**
     * Starts the game loop.
     */
    public void start() {
        lastTime = System.nanoTime();
        timer.start();
    }

    /**
     * Stops the game loop.
     */
    public void stop() { timer.stop(); }
}
