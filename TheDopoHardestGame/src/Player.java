package domain;

import java.awt.Color;

/**
 * Represents a player in the game. <br>
 * <b>(name, deaths, spawnX, spawnY)</b> <br>
 * <b>Inv:</b> deaths >= 0
 */
public abstract class Player extends MovableElement {
    protected String name;
    protected int deaths;
    protected double spawnX;
    protected double spawnY;
    protected boolean hasCheckpoint;

    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
        this.hasCheckpoint = false;
    }

    public void markCheckpoint(double x, double y) {
        setSpawnPoint(x, y);
        this.hasCheckpoint = true;
    }

    public boolean hasCheckpoint() {
        return hasCheckpoint;
    }

    public void move(double dx, double dy, Level level) {
        double nextX = this.x + (dx * this.speed);
        double nextY = this.y + (dy * this.speed);
        if (level.isWalkable(nextX, nextY, this.width, this.height)) {
            this.setPosition(nextX, nextY);
        }
    }

    public void die() {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
    }

    public abstract void changeSkin(String newSkin);

    public abstract Color getDisplayColor();

    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    public int getDeaths() { return deaths; }
    public String getName() { return name; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
}
