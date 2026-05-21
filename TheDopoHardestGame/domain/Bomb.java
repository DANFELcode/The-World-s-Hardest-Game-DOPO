package domain;
import dto.DrawCommand;

import java.awt.Color;

public class Bomb extends StaticElement implements Lethal {

    private boolean exploded = false;

    public Bomb(double x, double y, double width, double height) {
        super(x, y, width, height, "Purple");
    }

    public boolean hasExploded() {
        return exploded;
    }

    @Override
    public void onContact(Player player, Level level) {
        onDestroy(player, level);
    }

    @Override
    public void onDestroy(Player player, Level level) {
        if (!exploded) {
            this.exploded = true;
            player.onHit(level);
        }
    }

    @Override
    public void onContact(Enemy enemy, Level level) {
        if (!exploded) {
            this.exploded = true;
            enemy.die();
        }
    }

    @Override
    public Color getDisplayColor() {
        return GameConstants.COLOR_BOMB;
    }

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), DrawCommand.Shape.OVAL);
    }

    /** An exploded bomb stays in the level but turns invisible, so it can be restored on respawn. */
    @Override
    public boolean isVisible() { return !exploded; }

    /** Never removed: the bomb is kept so {@link #reset()} can bring it back. */
    @Override
    public boolean shouldBeRemoved() { return false; }

    /** Restores the bomb to its unexploded state (called when a player respawns). */
    @Override
    public void reset() { exploded = false; }

    @Override
    public String getFileType() { return "BOMB"; }
}
