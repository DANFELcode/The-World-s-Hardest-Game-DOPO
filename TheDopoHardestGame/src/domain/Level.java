package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a game level. <br>
 * <b>(number, gameTime, map, enemies, coins, staticElements, zones)</b> <br>
 * <b>Inv:</b> number >= 0 and gameTime > 0 and map != null
 */
public class Level {

    private int number;
    /** Remaining time in ticks. Presentation translates to seconds for display. */
    private int gameTime;
    /** Original time in ticks as loaded from file. Never decreases. */
    private int initialGameTime;
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
    private ArrayList<double[]> recentExplosions = new ArrayList<>();

    public Level(int number, int gameTimeInTicks, GameMap map) {
        this.number = number;
        this.gameTime = gameTimeInTicks;
        this.initialGameTime = gameTimeInTicks;
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

    /** Records a bomb explosion center so the presentation layer can render the effect. */
    public void recordExplosion(double cx, double cy) {
        recentExplosions.add(new double[]{cx, cy});
    }

    /** Returns all recorded explosion positions since the last call, then clears the list. */
    public List<double[]> drainExplosions() {
        List<double[]> drained = new ArrayList<>(recentExplosions);
        recentExplosions.clear();
        return drained;
    }

    public void updateLevel() {
        if (isPaused) return;
        moveEnemies();
        automatePlayers();
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

    /** Calls automate() on every player. Players without a strategy are no-op; AI players move via their strategy. */
    private void automatePlayers() {
        for (Player player : players) {
            player.automate(this);
        }
    }

    /**
     * Marks all currently-collected coins owned by the given player as protected by checkpoint.
     * Called when a player reaches an IntermediateZone. Only coins collected up to this point
     * are preserved on future deaths; coins collected after this point still reset.
     * @param player the player that reached the checkpoint
     */
    public void protectCollectedCoins(Player player) {
        String name = player.getName();
        for (Coin coin : coins) {
            if (name.equals(coin.getOwnerName()) && coin.isCollected()) {
                coin.protectByCheckpoint();
            }
        }
    }

    /** Applies death-penalty rules: resets the dying player's owned coins and collectibles. */
    public void onPlayerDeath(Player player) {
        String name = player.getName();
        for (Coin coin : coins) {
            if (name.equals(coin.getOwnerName()) && (!coin.isProtectedByCheckpoint() || coin.resetsOnAnyDeath())) {
                coin.reset();
            }
        }
        for (StaticElement element : staticElements) {
            String owner = element.getOwnerName();
            // Reset the dying player's owned elements, and unowned shared hazards (bombs).
            if (owner == null || owner.equals(name)) {
                element.reset();
            }
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

    /** Returns the original time in ticks as set when the level was loaded. Never decreases. */
    public int getInitialGameTime() { return initialGameTime; }

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
    public void setMap(GameMap map) { this.map = map; }

    /** @return an unmodifiable view of the players; mutate via addPlayer. */
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    /** @return an unmodifiable view of the enemies; mutate via addEnemy. */
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }

    /** @return an unmodifiable view of the coins; mutate via addCoin. */
    public List<Coin> getCoins() { return Collections.unmodifiableList(coins); }

    /** @return an unmodifiable view of the static elements; mutate via addStaticElement. */
    public List<StaticElement> getStaticElements() { return Collections.unmodifiableList(staticElements); }

    /** @return an unmodifiable view of the zones; mutate via addZone. */
    public Map<String, Zone> getZones() { return Collections.unmodifiableMap(zones); }


}
