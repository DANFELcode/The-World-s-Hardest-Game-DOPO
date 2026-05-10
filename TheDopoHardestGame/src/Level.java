package domain;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a game level. <br>
 * <b>(number, gameTime, map, enemies, coins, staticElements, zones)</b> <br>
 * <b>Inv:</b> number >= 0 and gameTime > 0 and map != null
 */
public class Level {

    private int number;
    private double gameTime;
    private GameMap map;
    private ArrayList<Player> players;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private ArrayList<StaticElement> staticElements;
    private HashMap<String, Zone> zones;

    /**
     * Creates a level with a number, time limit and map.
     * @param number level identifier, must be >= 0
     * @param gameTime time limit for the level, must be greater than 0
     * @param map the level map
     */
    public Level(int number, double gameTime, GameMap map) {
        this.number = number;
        this.gameTime = gameTime;
        this.map = map;
        this.players = new ArrayList<Player>();
        this.enemies = new ArrayList<Enemy>();
        this.coins = new ArrayList<Coin>();
        this.staticElements = new ArrayList<StaticElement>();
        this.zones = new HashMap<String, Zone>();
    }

    /**
     * Updates the level state: moves enemies, checks collisions and updates time.
     */
    public void updateLevel() {
    	// mover a cada enemigo
        for (Enemy enemy : enemies) {
            enemy.move(this);
        }
        // Colisiones enemigo-jugador
        for (Enemy enemy : enemies) {
            for (Player player : players) {
                if (enemy.getAreaColision().intersects(player.getAreaColision())) {
                    enemy.onDestroy(player);
                    if (!player.hasCheckpoint()) {
                        for (Coin coin : coins) coin.reset();
                    }
                }
            }
        }
        // Colisiones moneda-jugador
        for (Coin coin : coins) {
            if (!coin.isCollected()) {
                for (Player player : players) {
                    if (coin.getAreaColision().intersects(player.getAreaColision())) {
                        coin.onCollect(player);
                    }
                }
            }
        }
        // Colisiones zona-jugador
        for (Zone zone : zones.values()) {
            for (Player player : players) {
                Rectangle2D zoneRect = new Rectangle2D.Double(zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight());
                Rectangle2D playerRect = player.getAreaColision();
                if (zoneRect.intersects(playerRect)) {
                    zone.onPlayerEnter(player);
                }
            }
        }
        if (gameTime > 0) gameTime -= 1.0 / 60.0;
    }

    public double getGameTime() { return gameTime; }    


    /**
     * Returns whether the level is complete: final zone visited and all coins collected.
     * @return true if the level is complete
     */
    public boolean isLevelComplete() {
        if (!isCoinsCollected()) return false;
        Zone fZone = zones.get("final");
        if (fZone == null) return false;
        Rectangle2D fzRect = new Rectangle2D.Double(
            fZone.getX(), fZone.getY(), fZone.getWidth(), fZone.getHeight());
        for (Player p : players) {
            if (fzRect.intersects(p.getAreaColision())) return true;
        }
        return false;
    }

    /**
     * Returns whether all coins in the level have been collected.
     * @return true if all coins are collected
     */
    public boolean isCoinsCollected() {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    /**
     * Adds a player to the level.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        Zone initial = zones.get("initial");
        if (initial != null) {
            player.setSpawnPoint(initial.getX(), initial.getY());
            player.setPosition(initial.getX(), initial.getY());
        }
        players.add(player);
    }

    /**
     * Adds a coin to the level.
     * @param coin the coin to add (also use this method for SkinCoin, since it extends Coin
     *             and must be tracked by isCoinsCollected())
     */
    public void addCoin(Coin coin) { coins.add(coin); }

    /**
     * Adds an enemy to the level.
     * @param enemy the enemy to add
     */
    public void addEnemy(Enemy enemy) { enemies.add(enemy); }

    /**
     * Adds a static element to the level.
     * @param sElement the static element to add
     */
    public void addStaticElement(StaticElement sElement) { staticElements.add(sElement); }

    /**
     * Adds a zone to the level.
     * @param type zone type identifier (e.g. "initial", "intermediate", "final")
     * @param zone the zone to add
     */
    public void addZone(String type, Zone zone) {
        zones.put(type, zone);
        if ("initial".equals(type)) {
            for (Player p : players) {
                p.setSpawnPoint(zone.getX(), zone.getY());
                p.setPosition(zone.getX(), zone.getY());
            }
        }
    }

    /**
     * Returns whether there is a SolidWall at the given position.
     * @param x horizontal position
     * @param y vertical position
     * @return true if a SolidWall occupies that position
     */
    public boolean isWall(double x, double y) {
        for (StaticElement e : staticElements) {
            if (e.isBlocking() && e.getAreaColision().contains(x, y)) return true;
        }
        return false;
    }

    /**
     * Returns whether the given position is walkable: within map bounds and no SolidWall.
     * @param x horizontal position
     * @param y vertical position
     * @param width element width
     * @param height element height
     * @return true if the position is walkable
     */
    public boolean isWalkable(double x, double y, double width, double height) {
        if (x < 0 || y < 0 || x + width > map.getWidth() || y + height > map.getHeight()) return false;
        return !isWall(x, y)
        		&& !isWall(x + width - 1, y)
        		&& !isWall(x, y + height - 1)
        		 && !isWall(x + width - 1, y + height - 1);
    }

    public int getNumber() { return number; }
    public GameMap getMap() { return map; }
    public ArrayList<Player> getPlayers() { return players; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public void setMap(GameMap map) { this.map = map; }
    
    public ArrayList<Coin> getCoins() { return coins; }
    public ArrayList<StaticElement> getStaticElements() { return staticElements; }
    public HashMap<String, Zone> getZones() { return zones; }
}
