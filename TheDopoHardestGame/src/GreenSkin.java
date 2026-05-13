package domain;

import java.awt.Color;

public class GreenSkin implements SkinBehavior {

    private static final double SPEED = 1.0 * MovableElement.UNIT;
    private static final double SIZE = 20.0;

    private boolean isWeakened = false;

    @Override
    public void apply(Player player) {
        player.setSpeed(SPEED);
        player.setWidth(SIZE);
        player.setHeight(SIZE);
        this.isWeakened = false;
    }

    @Override
    public void onHit(Player player) {
        if (!isWeakened) {
            isWeakened = true;
            player.setSpeed(SPEED * 0.7);
        } else {
            player.die();
        }
    }

    @Override
    public Color getDisplayColor() {
        return isWeakened ? new Color(144, 238, 144) : new Color(0, 150, 0);
    }
}
