package domain;

import java.awt.Color;

/**
 * Represents a player in the game. <br>
 * <b>(name, deaths, spawnX, spawnY)</b> <br>
 * <b>Inv:</b> deaths >= 0
 */
public abstract class Player extends MovableElement implements Drawable {
    protected String name;
    protected int deaths;
    protected double spawnX;
    protected double spawnY;
    protected boolean hasCheckpoint;
    protected SkinBehavior currentSkin;
    protected long lastHitTime = 0;
    protected static final long INVULNERABILITY_TIME = 300;

    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
        this.hasCheckpoint = false;
        changeSkin(createDefaultSkin());
    }

    /** Each player subclass must declare its own initial skin. */
    protected abstract SkinBehavior createDefaultSkin();

    public void markCheckpoint(double x, double y) {
        setSpawnPoint(x, y);
        this.hasCheckpoint = true;
    }

    public boolean hasCheckpoint() {
        return hasCheckpoint;
    }

    public void move(double dx, double dy, Level level) {
        double nextX = getX() + (dx * getSpeed());
        double nextY = getY() + (dy * getSpeed());
        if (level.isWalkable(nextX, nextY, getWidth(), getHeight())) {
            this.setPosition(nextX, nextY);
        }
    }

    /** Raw death: increments counter and repositions to spawn. */
    public void die() {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
    }

    /** Called by the game when the player is hit by an enemy or hazard. */
    public void onHit() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < INVULNERABILITY_TIME) return;
        lastHitTime = now;
        currentSkin.onHit(this);
    }

    public void changeSkin(SkinBehavior skin) {
        this.currentSkin = skin;
        skin.apply(this);
    }

    /** Restores the player to its original skin (the one defined by createDefaultSkin). */
    public void restoreSkin() {
        changeSkin(createDefaultSkin());
    }

    public Color getDisplayColor() {
        return currentSkin.getDisplayColor();
    }

    /** Returns the player's type identifier used in level files (e.g. "red", "blue", "green"). */
    public abstract String getTypeName();

    @Override
    public DrawCommand toDrawCommand() {
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(),
                DrawCommand.Shape.RECT, Color.BLACK);
    }

    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    public int getDeaths() { return deaths; }
    public String getName() { return name; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
}
