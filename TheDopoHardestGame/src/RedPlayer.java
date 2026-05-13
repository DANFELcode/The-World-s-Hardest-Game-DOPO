package domain;

import java.awt.Color;

public class RedPlayer extends Player {

    private long lastHitTime = 0;

    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0 * UNIT);
        changeSkin(new DefaultSkin());
    }

    @Override
    public void onHit() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < INVULNERABILITY_TIME) return;
        lastHitTime = now;
        currentSkin.onHit(this);
    }

    @Override
    public void restoreSkin() {
        changeSkin(new DefaultSkin());
    }

    @Override
    public Color getDisplayColor() {
        return currentSkin.getDisplayColor();
    }

    @Override
    public String getTypeName() { return "red"; }
}
