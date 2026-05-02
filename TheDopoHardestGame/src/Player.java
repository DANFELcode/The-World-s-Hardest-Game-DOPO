package domain;

/**
 * Clase abstracta que define el comportamiento base de cualquier jugador
 */
public abstract class Player extends MovableElement {
    protected String name;
    protected int deaths;
    protected int coinsCollected;
    protected double spawnX;
    protected double spawnY;

    public Player(String name, double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed);
        this.name = name;
        this.spawnX = x;
        this.spawnY = y;
        this.deaths = 0;
        this.coinsCollected = 0;
    }

    public void move(double dx, double dy, Map map) {
        double nextX = this.x + (dx * this.speed);
        double nextY = this.y + (dy * this.speed);

        if (map.isWalkable(nextX, nextY, this.width, this.height)) {
            this.setPosition(nextX, nextY);
        }
    }

    public void die() {
        this.deaths++;
        this.setPosition(spawnX, spawnY);
    }

    public void addCoin() {
        this.coinsCollected++;
    }

    public abstract void changeSkin(String newSkin);

    public void setSpawnPoint(double newSpawnX, double newSpawnY) {
        this.spawnX = newSpawnX;
        this.spawnY = newSpawnY;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public String getName() {
        return name;
    }
}