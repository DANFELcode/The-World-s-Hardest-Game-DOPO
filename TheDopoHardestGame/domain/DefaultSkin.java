package domain;

import java.awt.Color;

public class DefaultSkin implements SkinBehavior {

    private static final double SPEED = 1.0 * MovableElement.UNIT;
    private static final double SIZE = 20.0;

    @Override
    public void apply(Player player) {
        player.setSpeed(SPEED);
        player.setWidth(SIZE);
        player.setHeight(SIZE);
    }

    @Override
    public void onHit(Player player) {
        player.die();
    }
    
    @Override
    public Color getDisplayColor() {
        return Color.RED;
    }
}
