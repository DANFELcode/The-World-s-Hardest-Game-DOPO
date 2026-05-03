package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class that controls the game flow, manages the selected game mode
 * and acts as intermediary with the presentation layer. <br>
 * <b>(currentLevel, currentGameMode, isPaused)</b> <br>
 * <b>Inv:</b> currentGameMode != null
 */
public class TheDOPOHardestGame {

    private Level currentLevel;
    private GameMode currentGameMode;
    private boolean isPaused;
    
    public enum GameMode {
        PLAYER(1, false),
        PvsP(2, false),
        PvsM(1, true);

        private final int playerCount;
        private final boolean hasMachine;

        GameMode(int playerCount, boolean hasMachine) {
            this.playerCount = playerCount;
            this.hasMachine = hasMachine;
        }

        public int getPlayerCount() { return playerCount; }
        public boolean hasMachine() { return hasMachine; }
    }

    /**
     * Creates a new game instance with default mode (PLAYER) and unpaused.
     */
    public TheDOPOHardestGame() {
        this.currentGameMode = GameMode.PLAYER;
        this.isPaused = false;
    }

    /**
     * Sets the game mode.
     * @param mode the game mode to set
     */
    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    /**
     * Initialises a level with the given number, time limit and map.
     * @param levelNumber the level identifier
     * @param gameTime the time limit for the level
     * @param map the map for the level
     */
    public void startLevel(int levelNumber, double gameTime, GameMap map) {
        this.currentLevel = new Level(levelNumber, gameTime, map);
    }

    /**
     * Updates the logical state of the game (movements, collisions, time).
     * Must be called continuously by the presentation game loop.
     */
    public void update() {
        if (!isPaused && currentLevel != null) {
            currentLevel.updateLevel();
        }
    }

    /**
     * Moves a specific player by index.
     * @param playerIndex index of the player to move
     * @param dx horizontal direction (-1, 0 or 1)
     * @param dy vertical direction (-1, 0 or 1)
     */
    public void movePlayer(int playerIndex, double dx, double dy) {
        if (!isPaused && currentLevel != null) {
            List<Player> players = currentLevel.getPlayers();
            if (playerIndex >= 0 && playerIndex < players.size()) {
                players.get(playerIndex).move(dx, dy, currentLevel);
            }
        }
    }

    /**
     * Returns whether the current level has been completed successfully.
     * @return true if the level is complete
     */
    public boolean isLevelComplete() {
        return currentLevel != null && currentLevel.isLevelComplete();
    }

    /**
     * Returns the list of players in the current level.
     * @return list of players, or empty list if no level is loaded
     */
    public List<Player> getPlayers() {
        if (currentLevel != null) return currentLevel.getPlayers();
        return new ArrayList<>();
    }

    /**
     * Toggles the pause state of the game.
     */
    public void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public GameMode getGameMode() { return currentGameMode; }
    public Level getCurrentLevel() { return currentLevel; }

    /**
     * Defines the available game modes. <br>
     * PLAYER: single player. PvsP: two players. PvsM: player vs machine.
     */

}
