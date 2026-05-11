package domain;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class that controls the game flow, manages the selected game mode
 * and acts as intermediary with the presentation layer. <br>
 * <b>(currentLevel, currentGameMode, isPaused)</b> <br>
 * <b>Inv:</b> currentGameMode != null
 */
public class TheDOPOHardestGame {

    private static TheDOPOHardestGame instance;

    private Level currentLevel;
    private GameMode currentGameMode;
    private boolean isPaused;

    public static TheDOPOHardestGame getInstance() {
        if (instance == null) instance = new TheDOPOHardestGame();
        return instance;
    }
    private int currentLevelNumber = 1; 
    
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

    private TheDOPOHardestGame() {
        this.currentGameMode = GameMode.PLAYER;
        this.isPaused = false;
    }

    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    public void startLevel(int levelNumber, double gameTime, GameMap map) {
        this.currentLevel = new Level(levelNumber, gameTime, map);
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
        return currentLevel != null && currentLevel.isLevelComplete();
    }

    public boolean isGameOver() {
        return currentLevel != null && currentLevel.getGameTime() <= 0 && !currentLevel.isLevelComplete();
    }

    public List<Player> getPlayers() {
        if (currentLevel != null) return currentLevel.getPlayers();
        return new ArrayList<>();
    }

    public void togglePause() {
        this.isPaused = !this.isPaused;
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

    public double getRemainingTime() {
        if (currentLevel == null) return 0.0;
        return currentLevel.getGameTime();
    }
    public int getCurrentLevelNumber() { return currentLevelNumber; }

    /**
     * Advances to the next logical level.
     */
    public void advanceLevel() {
        if (currentLevelNumber == 1) {
            currentLevelNumber = 2;
            loadLevelTwo();
        } else {
            currentLevelNumber = 1;
            loadTestLevel();
        }
    }
    
    public void loadTestLevel() { 
        GameMap map = new GameMap(800, 500);
        currentLevel = new Level(1, 90.0, map);

        RedPlayer player = new RedPlayer("Player", 60, 240);
        currentLevel.addPlayer(player);

        currentLevel.addStaticElement(new SolidWall(0, 0, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 480, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 0, 20, 500, "black"));
        currentLevel.addStaticElement(new SolidWall(780, 0, 20, 500, "black"));

        currentLevel.addStaticElement(new SolidWall(150, 100, 20, 200, "black"));
        currentLevel.addStaticElement(new SolidWall(300, 200, 20, 280, "black"));
        currentLevel.addStaticElement(new SolidWall(450, 20, 20, 280, "black"));
        currentLevel.addStaticElement(new SolidWall(600, 200, 20, 280, "black"));

        currentLevel.addCoin(new Coin(220, 250, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(370, 100, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(520, 350, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(670, 100, 15, 15, "yellow"));

        currentLevel.addCoin(new SkinCoin(380, 380, 15, 15, "Blue"));

        currentLevel.addEnemy(new Enemy(180, 60, 20, 20,
            LinearMovement.basic(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(330, 250, 20, 20,
            LinearMovement.basic(LinearMovement.Direction.VERTICAL, -1)));
        currentLevel.addEnemy(new Enemy(480, 60, 20, 20,
            LinearMovement.basic(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(630, 250, 20, 20,
            LinearMovement.basic(LinearMovement.Direction.VERTICAL, -1)));

        currentLevel.addZone("initial", new InitialZone(30, 200, 100, 100));
        currentLevel.addZone("intermediate", new IntermediateZone(380, 380, 60, 60));
        currentLevel.addZone("final", new FinalZone(680, 200, 90, 100));
    }

    /**
     * Nivel 2
     */
    public void loadLevelTwo() {
        GameMap map = new GameMap(800, 500);
        currentLevel = new Level(2, 120.0, map);

        RedPlayer player = new RedPlayer("Player", 40, 240);
        currentLevel.addPlayer(player);

        currentLevel.addStaticElement(new SolidWall(0, 0, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 480, 800, 20, "black"));
        currentLevel.addStaticElement(new SolidWall(0, 0, 20, 500, "black"));
        currentLevel.addStaticElement(new SolidWall(780, 0, 20, 500, "black"));

        currentLevel.addStaticElement(new SolidWall(100, 20, 250, 170, "black"));
        currentLevel.addStaticElement(new SolidWall(100, 310, 250, 170, "black"));

        currentLevel.addEnemy(new Enemy(140, 195, 20, 20, LinearMovement.basic(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(200, 285, 20, 20, LinearMovement.basic(LinearMovement.Direction.VERTICAL, -1)));
        currentLevel.addEnemy(new Enemy(260, 195, 20, 20, LinearMovement.basic(LinearMovement.Direction.VERTICAL, 1)));
        currentLevel.addEnemy(new Enemy(320, 285, 20, 20, LinearMovement.basic(LinearMovement.Direction.VERTICAL, -1)));

        currentLevel.addCoin(new Coin(170, 245, 12, 12, "yellow"));
        currentLevel.addCoin(new Coin(290, 245, 12, 12, "yellow"));

        currentLevel.addZone("checkpoint", new IntermediateZone(360, 190, 70, 120));
        currentLevel.addEnemy(new Enemy(385, 195, 20, 20, LinearMovement.accelerated(LinearMovement.Direction.VERTICAL, 1)));

        currentLevel.addStaticElement(new SolidWall(690, 20, 20, 180, "black"));
        currentLevel.addStaticElement(new SolidWall(690, 300, 20, 180, "black"));

        Point2D.Double[] routeOuter = {
            new Point2D.Double(450, 40), new Point2D.Double(650, 40),
            new Point2D.Double(650, 440), new Point2D.Double(450, 440)
        };
        currentLevel.addEnemy(new Enemy(450, 40, 20, 20, PatrolMovement.basic(routeOuter)));

        Point2D.Double[] routeSweeper = {
            new Point2D.Double(450, 240), new Point2D.Double(650, 240)
        };
        currentLevel.addEnemy(new Enemy(450, 240, 20, 20, PatrolMovement.basic(routeSweeper)));

        currentLevel.addEnemy(new Enemy(735, 30, 20, 20, LinearMovement.accelerated(LinearMovement.Direction.VERTICAL, 1)));

        currentLevel.addCoin(new Coin(460, 50, 15, 15, "yellow"));
        currentLevel.addCoin(new Coin(640, 430, 15, 15, "yellow"));
        currentLevel.addCoin(new SkinCoin(545, 240, 15, 15, "Blue"));

        currentLevel.addZone("initial", new InitialZone(30, 200, 60, 100));
        currentLevel.addZone("final", new FinalZone(720, 200, 50, 100));
    }
}
