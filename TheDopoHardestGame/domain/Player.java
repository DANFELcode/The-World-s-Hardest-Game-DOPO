package domain;
import dto.DrawCommand;

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
    private boolean hasCheckpoint;
    protected SkinBehavior currentSkin;
    private String lastSkinType; // type of the last SkinCoin collected; survives checkpoint deaths
    protected int extraLives = 0;
    protected Color borderColor = Color.BLACK;
    private GameStrategy strategy; // null = controlled by human input

    private int coinsCollected;

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

    /** Factory method: creates the appropriate Player subclass for the given type identifier. */
    public static Player create(String type, String name, double x, double y) {
        switch (type) {
            case "blue":  return new BluePlayer(name, x, y);
            case "green": return new GreenPlayer(name, x, y);
            default:      return new RedPlayer(name, x, y);
        }
    }
    
    // se deja metodo por extension futura
    /** No-op hook called when a coin is picked up. Per-level count lives in Level. */
    public void collectCoin() { }

    /** Adds the given amount to the player's lifetime coin total (snapshot on level end). */
    public void addToLifetime(int amount) {
        coinsCollected += amount;
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
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 1.0) {
            dx /= length;
            dy /= length;
        }
        double nextX = getX() + (dx * getSpeed());
        double nextY = getY() + (dy * getSpeed());
        if (level.isWalkable(nextX, nextY, getWidth(), getHeight())) {
            this.setPosition(nextX, nextY);
        }
    }

    /** Single entry point for death: increments counter, repositions, restores skin, resets coins. */
    public void die(Level level) {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
        if (hasCheckpoint && lastSkinType != null) {
            // With a checkpoint, keep the last collected skin (fresh instance, no stale state).
            changeSkin(SkinBehavior.of(lastSkinType));
        } else {
            restoreSkin();
            lastSkinType = null;
        }
        level.onPlayerDeath(this);
    }

    /** Records the type of the last collected SkinCoin (restored on checkpoint deaths). */
    public void setLastSkin(String skinType) { this.lastSkinType = skinType; }

    /** Called by the game when the player is hit by an enemy or hazard. */
    public void onHit(Level level) {
        if (level.isInSafeZone(this)) return;
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
        Color outer = (extraLives > 0) ? GameConstants.COLOR_LIFESOURCE : null;
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(),
                DrawCommand.Shape.RECT, borderColor, outer);
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
        // Zones are safe sanctuaries: no player-player kills inside any zone.
        // This also breaks the respawn-loop when both share a checkpoint.
        if (level.isInSafeZone(this) || level.isInSafeZone(other)) return;
        this.die(level);
        other.die(level);
    }
    
    /** If this player has a strategy, the strategy decides its movement this tick. Otherwise no-op. */
    public void automate(Level level) {
        if (strategy != null) strategy.execute(this, level);
    }

    /** Attaches an AI strategy to this player. Pass null to revert to human control. */
    public void setStrategy(GameStrategy strategy) { this.strategy = strategy; }

    public boolean isMachine() { return strategy != null; }
    
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void restoreLifetime(int coins) { this.coinsCollected = coins; }

    public int getDeaths() { return deaths; }
    public String getName() { return name; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
    public int getCoinsCollected() { return coinsCollected; }
    
}
