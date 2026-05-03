package domain;

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
        // TODO: move enemies
        // TODO: check collisions
        // TODO: update time
    }

    /**
     * Returns whether the level is complete: final zone visited and all coins collected.
     * @return true if the level is complete
     */
    public boolean isLevelComplete() {
        Zone fZone = zones.get("final");
        return fZone != null && fZone.isVisited() && isCoinsCollected();
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
    public void addPlayer(Player player) { players.add(player); }

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
    public void addZone(String type, Zone zone) { zones.put(type, zone); }

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
}
