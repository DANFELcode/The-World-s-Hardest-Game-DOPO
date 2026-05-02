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

    public int getNumber() { return number; }
    public GameMap getMap() { return map; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public void setMap(GameMap map) { this.map = map; }
}
