package presentation;

import domain.GameDataAccess;
import javax.swing.Timer;

/**
 * Drives the game using a fixed-step accumulator pattern.
 * Domain ticks are dispatched at a fixed rate regardless of real-frame timing,
 * which keeps game speed deterministic across hardware. <br>
 * <b>Inv:</b> gui != null and TICKS_PER_SECOND > 0
 */
public class GameLoop {

    private static final double SECONDS_PER_TICK = 1.0 / GameDataAccess.TICKS_PER_SECOND;
    private static final int MAX_TICKS_PER_FRAME = 5; // spiral-of-death cap

    /** Visual frame poll rate. Doesn't affect game speed — only smoothness of rendering. */
    private static final int FRAME_DELAY_MS = 1000 / GameDataAccess.TICKS_PER_SECOND;

    private final Timer timer;
    private final TheDOPOHardestGameGUI gui;

    private long lastTime;
    private double accumulator;

    public GameLoop(TheDOPOHardestGameGUI gui) {
        this.gui = gui;
        this.timer = new Timer(FRAME_DELAY_MS, e -> tick());
    }

    private void tick() {
        long now = System.nanoTime();
        double elapsed = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        accumulator += elapsed;
        int ticksRun = 0;
        while (accumulator >= SECONDS_PER_TICK && ticksRun < MAX_TICKS_PER_FRAME) {
            gui.update();
            accumulator -= SECONDS_PER_TICK;
            ticksRun++;
        }
        // If we hit the cap, drop the backlog instead of trying to catch up.
        if (ticksRun == MAX_TICKS_PER_FRAME) accumulator = 0;

        gui.refresh();
    }

    public void start() {
        lastTime = System.nanoTime();
        accumulator = 0;
        timer.start();
    }

    public void stop() { timer.stop(); }
}
