package domain;

import java.awt.Color;

/**
 * Default skin for the red player: 1x speed and 20x20 size. Dies on the first hit. <br>
 * <b>Inv:</b> speed > 0 and size > 0
 */
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
    public void onHit(Player player, Level level) {
        player.die(level);
    }
    
    @Override
    public Color getDisplayColor() {
        return Color.RED;
    }

    @Override
    public String getSkinType() { return "red"; }
}
