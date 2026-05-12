package domain;

import java.awt.Color;

public class Bomb extends StaticElement implements Lethal {

    private boolean exploded = false; 

    public Bomb(double x, double y, double width, double height) {
        super(x, y, width, height, "Brown");
    }

    public boolean hasExploded() {
        return exploded;
    }

    @Override
    public void onContact(Player player, Level level) {
        if (!exploded) {
            this.exploded = true;
            player.die();
        }
    }
    
    @Override
    public Color getDisplayColor() {
        return new Color(160, 32, 240);
    }
    
    @Override
    public void onContact(Enemy enemy, Level level) {
        if (!exploded) {
            this.exploded = true;
            enemy.die();
        }
    }

    @Override
    public void onDestroy(Player player) {
        player.die();
    }
    
    @Override
    public boolean shouldBeRemoved() { return exploded; }
    
    @Override
    public boolean isBomb() { return true; }
}