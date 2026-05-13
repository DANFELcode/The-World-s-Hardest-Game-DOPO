package domain;

import java.awt.Color;

public class GreenPlayer extends Player {

    private static final double INITIAL_SPEED = 1.0 * UNIT;
    private boolean isWeakened = false;

    public GreenPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, INITIAL_SPEED);
    }

    @Override
    public void onHit() {
        if (!isWeakened) {
            this.isWeakened = true;
            setSpeed(INITIAL_SPEED * 0.5);
        } else {
            super.die();
            restoreStatus();
        }
    }

    public void restoreStatus() {
        this.isWeakened = false;
        setSpeed(INITIAL_SPEED);
    }

    @Override
    public Color getDisplayColor() {
        return isWeakened ? new Color(144, 238, 144) : new Color(0, 150, 0);
    }

    @Override
    public String getTypeName() { return "green"; }
}
