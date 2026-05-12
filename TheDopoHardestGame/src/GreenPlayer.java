package domain;

import java.awt.Color;

public class GreenPlayer extends Player {

    private static final double INITIAL_SPEED = 1.0 * UNIT;
    private boolean isWeakened = false;

    public GreenPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, INITIAL_SPEED);
    }

    @Override
    public void die() {
        if (!isWeakened) {
            this.isWeakened = true;
            this.speed = INITIAL_SPEED * 0.5;
            // No llama a super.die(): no aumenta deaths ni vuelve al spawn
        } else {
            super.die();
            restoreStatus();
        }
    }

    @Override
    public void changeSkin(String newSkin) {
        if ("Green".equalsIgnoreCase(newSkin)) {
            restoreStatus();
        }
    }

    public void restoreStatus() {
        this.isWeakened = false;
        this.speed = INITIAL_SPEED;
    }

    @Override
    public Color getDisplayColor() {
        return isWeakened ? new Color(144, 238, 144) : new Color(0, 150, 0);
    }
}
