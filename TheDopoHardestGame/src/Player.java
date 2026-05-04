package domain;

/**
 * Represents a player in the game. 
 * (name, deaths, spawnX, spawnY)
 * Inv: deaths >= 0
 */
public abstract class Player extends MovableElement {
    protected String name;
    protected int deaths;
    protected double spawnX;
    protected double spawnY;    


    /**
     * Creates a player with a name, initial position, size and speed.
     * @param name player name
     * @param x initial horizontal position
     * @param y initial vertical position
     * @param width player width, must be greater than 0
     * @param height player height, must be greater than 0
     * @param speed movement speed, must be greater than 0
     */
    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
    }

    /**
     * Moves the player by the given direction vector, if the destination is walkable.
     * @param dx horizontal direction (-1, 0 or 1)
     * @param dy vertical direction (-1, 0 or 1)
     * @param level the current level
     */
    public void move(double dx, double dy, Level level) {
        double nextX = this.x + (dx * this.speed);
        double nextY = this.y + (dy * this.speed);
        if (level.isWalkable(nextX, nextY, this.width, this.height)) {
            this.setPosition(nextX, nextY);
        }
    }

    /**
     * Increments the death counter and respawns the player at the spawn point.
     */
    public void die() {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
    }

    /**
     * Changes the player's skin to the given color.
     * @param newSkin the color of the new skin
     */
    public abstract void changeSkin(String newSkin);

    /**
     * Updates the player's spawn point.
     * @param newSpawnX new horizontal spawn position
     * @param newSpawnY new vertical spawn position
     */
    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    public int getDeaths() { return deaths; }
    public String getName() { return name; }
}
