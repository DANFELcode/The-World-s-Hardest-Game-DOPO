package domain;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class that controls the game flow, manages the selected game mode
 * and acts as intermediary with the presentation layer. <br>
 * <b>(currentLevel, currentGameMode, isPaused)</b> <br>
 * <b>Inv:</b> currentGameMode != null
 */
public class TheDOPOHardestGame implements Serializable {

    private Level currentLevel;
    private GameMode currentGameMode;
    private boolean isPaused;
    private int currentLevelNumber = 1;
    private transient GameDataAccess dataAccess = GameDataAccess.getInstance();
    private Map<String, Integer> levelsWon;
    private Map<String, String> playerTypes;
    private Map<String, java.awt.Color> playerBorderColors;

    //guardar solo el nivel en el que quedo, guardar solo lo necesario no todo el juego
    public TheDOPOHardestGame() {
        this.currentGameMode = GameMode.PLAYER;
        this.isPaused = false;
        this.levelsWon = new HashMap<String, Integer>();
        this.playerTypes = new HashMap<String, String>();
        this.playerBorderColors = new HashMap<String, java.awt.Color>();
    }

    public void startGame() {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        currentLevelNumber = 1;
        levelsWon = new HashMap<>();
        currentLevel = dataAccess.loadLevel("level" + currentLevelNumber + ".txt", currentGameMode);
        applyModeRules(currentLevel);
        createPlayers();
    }

    private void applyModeRules(Level level) {
        if (level != null && currentGameMode == GameMode.PvsP) {
            level.setHasTimer(false);
        }
    }

    private void createPlayers() {
        for (Map.Entry<String, Zone> entry : currentLevel.getZones().entrySet()) {
            String key = entry.getKey();
            Zone zone = entry.getValue();
            if (key.startsWith("initial_")) {
                String owner = key.replace("initial_", "");
                String type = playerTypes.getOrDefault(owner, "red");
                Player player;
                switch (type) {
                    case "blue":
                        player = new BluePlayer(owner, zone.getX(), zone.getY());
                        break;
                    case "green":
                        player = new GreenPlayer(owner, zone.getX(), zone.getY());
                        break;
                    default:
                        player = new RedPlayer(owner, zone.getX(), zone.getY());
                        break;
                }
                java.awt.Color border = playerBorderColors.get(owner);
                if (border != null) player.setBorderColor(border);
                currentLevel.addPlayer(player);
                assignCoinOwners(currentLevel, player);
            }
        }
    }

    private void assignCoinOwners(Level level, Player player) {
        for (Coin c : level.getCoins()) {
            if (player.getName().equals(c.getOwnerName())) {
                c.setOwnerPlayer(player);
            }
        }
    }

    public void setPlayerBorderColor(String owner, java.awt.Color color) {
        this.playerBorderColors.put(owner, color);
    }

	public void guardarPartida(File file) throws GameException {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        dataAccess.guardarPartida(this, file);
    }

    public void abrirPartida(File file) throws GameException {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        TheDOPOHardestGame loaded = dataAccess.abrirPartida(file);
        this.currentLevel = loaded.currentLevel;
        this.currentGameMode = loaded.currentGameMode;
        this.isPaused = loaded.isPaused;
        this.currentLevelNumber = loaded.currentLevelNumber;
    }

    public void exportarNivel(File file) throws GameException {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        if (currentLevel != null) {
            dataAccess.exportarNivel(currentLevel, file);
        }
    }

    public void importarNivel(File file) throws GameException {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        Level level = dataAccess.loadLevelAbsolute(file);
        this.currentLevel = level;
        this.currentLevelNumber = level.getNumber();
    }

    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }
    
    public void setPlayerType(String owner, String type) {
    	this.playerTypes.put(owner, type);
    }

    public void startLevel(int levelNumber, int gameTimeInTicks, GameMap map) {
        this.currentLevel = new Level(levelNumber, gameTimeInTicks, map);
    }

    public void update() {
        if (!isPaused && currentLevel != null) {
            currentLevel.updateLevel();
        }
    }

    public void movePlayer(int playerIndex, double dx, double dy) {
        if (!isPaused && currentLevel != null) {
            List<Player> players = currentLevel.getPlayers();
            if (playerIndex >= 0 && playerIndex < players.size()) {
                players.get(playerIndex).move(dx, dy, currentLevel);
            }
        }
    }

    public boolean isLevelComplete() {
        return currentLevel != null && currentGameMode.isComplete(currentLevel);
    }

    public boolean isGameOver() {
        return currentLevel != null && currentLevel.hasTimer()
                && currentLevel.getGameTime() <= 0 && !currentLevel.isLevelComplete();
    }

    public List<Player> getPlayers() {
        if (currentLevel != null) return currentLevel.getPlayers();
        return new ArrayList<>();
    }

    public int getPlayerCoins(int playerIndex) {
        if (currentLevel == null) return 0;
        List<Player> players = currentLevel.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size()) return 0;
        return currentLevel.getCoinsCollectedCountBy(players.get(playerIndex));
    }

    /** Returns the total number of coin pickups this player has done across the game (re-pickups counted). */
    public int getPlayerLifetimeCoins(int playerIndex) {
        if (currentLevel == null) return 0;
        List<Player> players = currentLevel.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size()) return 0;
        return players.get(playerIndex).getCoinsCollected();
    }

    public int getPlayerTotalCoins(int playerIndex) {
        if (currentLevel == null) return 0;
        List<Player> players = currentLevel.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size()) return 0;
        String name = players.get(playerIndex).getName();
        int count = 0;
        for (Coin c : currentLevel.getCoins()) {
            if (name.equals(c.getOwnerName())) count++;
        }
        return count;
    }

    public void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public boolean isPaused() { return isPaused; }

    public void restartLevel() {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        currentLevel = dataAccess.loadLevel("level" + currentLevelNumber + ".txt", currentGameMode);
        applyModeRules(currentLevel);
        createPlayers();
        isPaused = false;
    }

    public GameMode getGameMode() { return currentGameMode; }
    public Level getCurrentLevel() { return currentLevel; }

    public int getPlayerDeaths(int playerIndex) {
        if (currentLevel == null) return 0;
        List<Player> players = currentLevel.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size()) return 0;
        return players.get(playerIndex).getDeaths();
    }

    public int getCollectedCoins() {
        if (currentLevel == null) return 0;
        int count = 0;
        for (Coin c : currentLevel.getCoins()) if (c.isCollected()) count++;
        return count;
    }

    public int getTotalCoins() {
        if (currentLevel == null) return 0;
        return currentLevel.getCoins().size();
    }

    public int getLevelNumber() {
        if (currentLevel == null) return 0;
        return currentLevel.getNumber();
    }

    /** Returns remaining time in seconds for presentation. Domain stores ticks internally. */
    public double getRemainingTime() {
        if (currentLevel == null) return 0.0;
        return currentLevel.getGameTime() / (double) GameDataAccess.TICKS_PER_SECOND;
    }
    
    public Map<String, Integer> getLevelsWon(){
    	return levelsWon;
    }
    
    public Player getLevelWinner() {
        if (currentLevel == null) return null;
        return currentLevel.getWinner();
    }

    public List<DrawCommand> getDrawCommands() {
        List<DrawCommand> commands = new ArrayList<>();
        if (currentLevel == null) return commands;
        for (Zone zone : currentLevel.getZones().values())
            commands.add(zone.toDrawCommand());
        for (StaticElement e : currentLevel.getStaticElements())
            commands.add(e.toDrawCommand());
        for (Coin coin : currentLevel.getCoins())
            if (!coin.isCollected()) commands.add(coin.toDrawCommand());
        for (Enemy enemy : currentLevel.getEnemies())
            commands.add(enemy.toDrawCommand());
        for (Player player : currentLevel.getPlayers())
            commands.add(player.toDrawCommand());
        return commands;
    }

    public int getCurrentLevelNumber() { return currentLevelNumber; }

    /** Returns true if there is a next level available for the current game mode. */
    public boolean hasNextLevel() {
        if (dataAccess == null) dataAccess = GameDataAccess.getInstance();
        return dataAccess.loadLevel("level" + (currentLevelNumber + 1) + ".txt", currentGameMode) != null;
    }

    /**
     * Advances to the next logical level.
     */
    public void advanceLevel() {
        List<Player> players = currentLevel.getPlayers();
        // Snapshot current level coin progress into each player's lifetime stat
        for (Player p : players) {
            p.addToLifetime(currentLevel.getCoinsCollectedCountBy(p));
        }
        currentLevelNumber++;
        Level next = dataAccess.loadLevel("level" + currentLevelNumber + ".txt", currentGameMode);
        Player winner = currentLevel.getWinner();
        if (winner != null) {
            levelsWon.merge(winner.getName(), 1, Integer::sum);
        }

        if (next == null) {
            // no hay más niveles entonces es victoria
        } else {
            applyModeRules(next);
            for (Player p : players) {
                p.hasCheckpoint = false;
                p.restoreSkin();
                Zone initial = next.getZones().get("initial_" + p.getName());
                if (initial != null) {
                    p.setSpawnPoint(initial.getX(), initial.getY());
                    p.setPosition(initial.getX(), initial.getY());
                }
                next.addPlayer(p);
                assignCoinOwners(next, p);
            }
            currentLevel = next;
        }
    }

}
