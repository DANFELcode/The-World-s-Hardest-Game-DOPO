package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class that controls the game flow, manages the selected game mode
 * and acts as intermediary with the presentation layer.
 * (currentLevel, currentGameMode, isPaused)
 * Inv: currentGameMode != null
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
     * Returns whether the player has lost (time ran out without completing the level).
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return currentLevel != null && currentLevel.getGameTime() <= 0 && !currentLevel.isLevelComplete();
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
    
    public void loadTestLevel() {
        GameMap map = new GameMap(800, 500);
        currentLevel = new Level(1, 90.0, map);

        // Player
        RedPlayer player = new RedPlayer("Player", 60, 240);
        currentLevel.addPlayer(player);

        // Border walls
        currentLevel.addStaticElement(new SolidWall(0, 0, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 480, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 0, 20, 500, "black"));
        currentLevel.addStaticElement(new SolidWall(780, 0, 20, 500, "black"));

        // Inner obstacles forming corridors
        currentLevel.addStaticElement(new SolidWall(150, 100, 20, 200, "black"));
        currentLevel.addStaticElement(new SolidWall(300, 200, 20, 280, "black"));
        currentLevel.addStaticElement(new SolidWall(450, 20, 20, 280, "black"));
        currentLevel.addStaticElement(new SolidWall(600, 200, 20, 280, "black"));

        // Coins
        currentLevel.addCoin(new Coin(220, 250, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(370, 100, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(520, 350, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(670, 100, 15, 15, "yellow"));

        // SkinCoin (Blue power-up)
        currentLevel.addCoin(new SkinCoin(380, 380, 15, 15, "Blue"));

        // Enemies bouncing vertically inside corridors
        currentLevel.addEnemy(new Enemy(180, 60, 20, 20, 1.0,
            new LinearMovement(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(330, 250, 20, 20, 1.0,
            new LinearMovement(LinearMovement.Direction.VERTICAL, -1)));
        currentLevel.addEnemy(new Enemy(480, 60, 20, 20, 1.0,
            new LinearMovement(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(630, 250, 20, 20, 1.0,
            new LinearMovement(LinearMovement.Direction.VERTICAL, -1)));

        // Zones
        currentLevel.addZone("initial", new InitialZone(30, 200, 100, 100));
        currentLevel.addZone("final", new FinalZone(680, 200, 90, 100));
    }

}
