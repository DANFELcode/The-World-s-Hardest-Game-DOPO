package domain;

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
        return new Color(160, 32, 240);
    }

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), DrawCommand.Shape.OVAL);
    }

    @Override
    public boolean shouldBeRemoved() { return exploded; }


    @Override
    public String getFileType() { return "BOMB"; }
}
