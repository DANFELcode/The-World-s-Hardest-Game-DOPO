package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a game level. <br>
 * <b>(number, gameTime, map, enemies, coins, staticElements, zones)</b> <br>
 * <b>Inv:</b> number >= 0 and gameTime > 0 and map != null
 */
public class Level implements Serializable {

    private int number;
    private double gameTime;
    private GameMap map;
    private ArrayList<Player> players;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private ArrayList<StaticElement> staticElements;
    private HashMap<String, Zone> zones;
    private ArrayList<Interactable> interactables;

    public Level(int number, double gameTime, GameMap map) {
        this.number = number;
        this.gameTime = gameTime;
        this.map = map;
        this.players = new ArrayList<Player>();
        this.enemies = new ArrayList<Enemy>();
        this.coins = new ArrayList<Coin>();
        this.staticElements = new ArrayList<StaticElement>();
        this.zones = new HashMap<String, Zone>();
        this.interactables = new ArrayList<Interactable>();
    }

    public void updateLevel(double deltaTime) {
        moveEnemies(deltaTime);
        resolvePlayerCollisions();

        interactables.removeIf(Interactable::shouldRemove);
        enemies.removeIf(Enemy::isDead);
        staticElements.removeIf(StaticElement::shouldBeRemoved);

        updateTime(deltaTime);
    }

    private void moveEnemies(double deltaTime) {
        for (Enemy enemy : enemies) {
            enemy.move(this, deltaTime);
        }
    }

    /**
     * Handles the penalty rules when a player dies in the level.
     * @param player the player that died
     */
    public void onPlayerDeath(Player player) {
        boolean atSpawn = Math.abs(player.getX() - player.getSpawnX()) < 0.01
                       && Math.abs(player.getY() - player.getSpawnY()) < 0.01;

        if (atSpawn) {
            if (!player.hasCheckpoint()) {
                for (Coin coin : coins) {
                    coin.reset();
                }
            }
        }
    }

    private void resolvePlayerCollisions() {
        for (Interactable element : interactables) {
            for (Player player : players) {
                if (element.getAreaColision().intersects(player.getAreaColision())) {
                    element.onPlayerContact(player, this);
                }
            }
        }
        for (StaticElement element : staticElements) {
            if (!element.isBlocking()) {
                for (Enemy enemy : enemies) {
                    if (element.getAreaColision().intersects(enemy.getAreaColision())) {
                        element.onContact(enemy, this);
                    }
                }
            }
        }
    }

    private void updateTime(double deltaTime) {
        if (gameTime > 0) gameTime -= deltaTime;
    }

    public double getGameTime() { return gameTime; }

    public boolean isLevelComplete() {
        if (!isCoinsCollected()) return false;
        Zone fZone = zones.get("final");
        if (fZone == null) return false;
        for (Player p : players) {
            if (fZone.getAreaColision().intersects(p.getAreaColision())) return true;
        }
        return false;
    }

    public boolean isCoinsCollected() {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    public void addPlayer(Player player) {
        Zone initial = zones.get("initial");
        if (initial != null) {
            player.setSpawnPoint(initial.getX(), initial.getY());
            player.setPosition(initial.getX(), initial.getY());
        }
        players.add(player);
    }

    public void addCoin(Coin coin) {
        coins.add(coin);
        interactables.add(coin);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        interactables.add(enemy);
    }

    public void addStaticElement(StaticElement sElement) {
        staticElements.add(sElement);
        interactables.add(sElement);
    }

    public void addZone(String type, Zone zone) {
        zones.put(type, zone);
        interactables.add(zone);
        if ("initial".equals(type)) {
            for (Player p : players) {
                p.setSpawnPoint(zone.getX(), zone.getY());
                p.setPosition(zone.getX(), zone.getY());
            }
        }
    }

    public boolean isBlocking(double x, double y) {
        for (StaticElement e : staticElements) {
            if (e.isBlocking() && e.getAreaColision().contains(x, y)) return true;
        }
        return false;
    }

    public boolean isWalkable(double x, double y, double width, double height) {
        if (x < 0 || y < 0 || x + width > map.getWidth() || y + height > map.getHeight()) return false;
        return !isBlocking(x, y)
                && !isBlocking(x + width - 1, y)
                && !isBlocking(x, y + height - 1)
                && !isBlocking(x + width - 1, y + height - 1);
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
