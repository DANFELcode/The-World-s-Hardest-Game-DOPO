package domain;

import java.awt.Color;

/**
 * Skin for the green player: absorbs the first hit by becoming weakened (0.7x speed).
 * Dies on the second hit. <br>
 * <b>Inv:</b> isWeakened in {true, false}
 */
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
    public void onHit(Player player, Level level) {
        if (!isWeakened) {
            isWeakened = true;
            player.setSpeed(SPEED * 0.7);
        } else {
            player.die(level);
        }
    }

    @Override
    public Color getDisplayColor() {
        return isWeakened ? GameConstants.COLOR_GREEN_WEAKENED : GameConstants.COLOR_GREEN_NORMAL;
    }

    @Override
    public String getSkinType() { return "green"; }
}
