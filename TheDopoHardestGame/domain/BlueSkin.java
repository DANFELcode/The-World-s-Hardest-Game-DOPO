package domain;

import java.awt.Color;

public class BlueSkin implements SkinBehavior {

    private static final double SPEED = 1.5 * MovableElement.UNIT;
    private static final double WIDTH = 30.0;
    private static final double HEIGHT = 30.0;

    @Override
    public void apply(Player player) {
        player.setSpeed(SPEED);
        player.setWidth(WIDTH);
        player.setHeight(HEIGHT);
    }

    @Override
    public void onHit(Player player, Level level) {
        player.die(level);
    }

    @Override
    public Color getDisplayColor() {
        return Color.BLUE;
    }

    @Override
    public String getSkinType() { return "blue"; }
}
