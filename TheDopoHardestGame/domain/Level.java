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
    /** Remaining time in ticks. Presentation translates to seconds for display. */
    private int gameTime;
    private boolean hasTimer = true;
    private boolean isPaused = false;
    private Player winner;
    private GameMap map;
    private ArrayList<Player> players;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private ArrayList<StaticElement> staticElements;
    private HashMap<String, Zone> zones;
    private ArrayList<Interactable> interactables;
    

    public Level(int number, int gameTimeInTicks, GameMap map) {
        this.number = number;
        this.gameTime = gameTimeInTicks;
        this.map = map;
        this.players = new ArrayList<Player>();
        this.enemies = new ArrayList<Enemy>();
        this.coins = new ArrayList<Coin>();
        this.staticElements = new ArrayList<StaticElement>();
        this.zones = new HashMap<String, Zone>();
        this.interactables = new ArrayList<Interactable>();
    }

    public void togglePause() { isPaused = !isPaused; }
    public boolean isPaused() { return isPaused; }

    public void updateLevel() {
        if (isPaused) return;
        moveEnemies();
        resolvePlayerCollisions();

        interactables.removeIf(Interactable::shouldRemove);
        enemies.removeIf(Enemy::isDead);
        staticElements.removeIf(StaticElement::shouldBeRemoved);

        updateTime();
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move(this);
        }
    }

    /** Applies death-penalty rules: resets the dying player's owned coins and collectibles. */
    public void onPlayerDeath(Player player) {
        boolean noCheckpoint = !player.hasCheckpoint();
        String name = player.getName();
        for (Coin coin : coins) {
            if (name.equals(coin.getOwnerName()) && (noCheckpoint || coin.resetsOnAnyDeath())) {
                coin.reset();
            }
        }
        for (StaticElement element : staticElements) {
            element.reset();
        }
    }

    private void resolvePlayerCollisions() {
        for (Interactable element : interactables) {
            for (Player player : players) {
                if (element != player && element.getAreaColision().intersects(player.getAreaColision())) {
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

    private void updateTime() {
        if (hasTimer && gameTime > 0) gameTime--;
    }

    public void setHasTimer(boolean hasTimer) { this.hasTimer = hasTimer; }
    public boolean hasTimer() { return hasTimer; }

    /** Returns remaining time in ticks. Presentation should translate to seconds. */
    public int getGameTime() { return gameTime; }

    public boolean isLevelComplete() {
        return hasWinner();
    }

    /** Returns true if the player is currently intersecting any zone (safe from hits). */
    public boolean isInSafeZone(Player player) {
        for (Zone zone : zones.values()) {
            if (zone.getAreaColision().intersects(player.getAreaColision())) return true;
        }
        return false;
    }

    public boolean isCoinsCollected() {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    /** Returns true if all coins owned by this player have been collected. */
    public boolean isCoinsCollectedBy(Player player) {
        String name = player.getName();
        for (Coin coin : coins) {
            if (name.equals(coin.getOwnerName()) && !coin.isCollected()) return false;
        }
        return true;
    }

    /** Returns how many of this player's coins are currently collected in the level. */
    public int getCoinsCollectedCountBy(Player player) {
        String name = player.getName();
        int count = 0;
        for (Coin coin : coins) {
            if (name.equals(coin.getOwnerName()) && coin.isCollected()) count++;
        }
        return count;
    }

    public void addPlayer(Player player) {
        players.add(player);
        interactables.add(player);
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
    
    public boolean hasWinner() {
    	return winner != null;
    }
    
    public void setWinner(Player player) {
    	this.winner = player;
    }

    public int getNumber() { return number; }
    public Player getWinner() {return winner;}
    public GameMap getMap() { return map; }
    public ArrayList<Player> getPlayers() { return players; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public void setMap(GameMap map) { this.map = map; }

    public ArrayList<Coin> getCoins() { return coins; }
    public ArrayList<StaticElement> getStaticElements() { return staticElements; }
    public HashMap<String, Zone> getZones() { return zones; }


}
