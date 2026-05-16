package domain;

import java.awt.Color;

/**
 * Represents a player in the game. <br>
 * <b>(name, deaths, spawnX, spawnY)</b> <br>
 * <b>Inv:</b> deaths >= 0
 */
public abstract class Player extends MovableElement implements Drawable, Interactable {
    protected String name;
    protected int deaths;
    protected double spawnX;
    protected double spawnY;
    protected boolean hasCheckpoint;
    protected SkinBehavior currentSkin;
    protected long lastHitTime = 0;
    protected int extraLives = 0;
    protected Color borderColor = Color.BLACK;

    private int coinsCollected;

    protected static final long INVULNERABILITY_TIME = 300;

    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
        this.hasCheckpoint = false;
        changeSkin(createDefaultSkin());
        this.coinsCollected = 0;
    }

    /** Each player subclass must declare its own initial skin. */
    protected abstract SkinBehavior createDefaultSkin();

    
    public void collectCoin() {
        coinsCollected++;
    }

    public void resetCoins() {
        coinsCollected = 0;
    }
    public void markCheckpoint(double x, double y) {
        setSpawnPoint(x, y);
        this.hasCheckpoint = true;
    }
    

    public boolean hasCheckpoint() {
        return hasCheckpoint;
    }
    
    public void resetCheckpoint() {
    	hasCheckpoint = false;
    }

    public void move(double dx, double dy, Level level) {
        double nextX = getX() + (dx * getSpeed());
        double nextY = getY() + (dy * getSpeed());
        if (level.isWalkable(nextX, nextY, getWidth(), getHeight())) {
            this.setPosition(nextX, nextY);
        }
    }

    /** Raw death: increments counter, repositions to spawn, and restores default skin. */
    public void die(Level level) {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
        restoreSkin();
    }

    /** Called by the game when the player is hit by an enemy or hazard. */
    public void onHit(Level level) {
        if (level.isInSafeZone(this)) return;
        long now = System.currentTimeMillis();
        if (now - lastHitTime < INVULNERABILITY_TIME) return;
        lastHitTime = now;
        if (extraLives > 0) {
            extraLives--;
            return;
        }
        currentSkin.onHit(this, level);
    }

    /** Adds an extra life to the player, which absorbs one hit. */
    public void addLife() {
        this.extraLives++;
    }

    public int getExtraLives() { return extraLives; }

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
        Color drawBorder = (extraLives > 0) ? new Color(255, 105, 180) : borderColor;
        long now = System.currentTimeMillis();
        Color displayColor = getDisplayColor();
        if (now - lastHitTime < INVULNERABILITY_TIME && (now / 50) % 2 == 0) {
            displayColor = Color.WHITE;
        }
        return new DrawCommand(displayColor, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(),
                DrawCommand.Shape.RECT, drawBorder);
    }

    public void setBorderColor(Color color) {
        if (color != null) this.borderColor = color;
    }

    public Color getBorderColor() { return borderColor; }

    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    @Override
    public void onPlayerContact(Player other, Level level) {
        this.die(level);
        other.die(level);
    }
    public int getDeaths() { return deaths; }
    public String getName() { return name; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
    public int getCoinsCollected() {return coinsCollected; }
    
}
