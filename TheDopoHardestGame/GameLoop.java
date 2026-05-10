package presentation;

import javax.swing.Timer;

public class GameLoop {

    // Para el ciclo 2 desacoplaremos GameLoop de la GUI y pondremos una interfaz updatable

    private Timer timer;
    private TheDOPOHardestGameGUI gui;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    public GameLoop(TheDOPOHardestGameGUI gui) {
        this.gui = gui;
        timer = new Timer(DELAY, e -> tick());
    }

    private void tick() {
        gui.update();
        gui.refresh();
    }

    public void start() { timer.start(); }
    public void stop()  { timer.stop();  }
}
