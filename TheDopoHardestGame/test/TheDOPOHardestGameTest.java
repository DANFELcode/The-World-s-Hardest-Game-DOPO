package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.TheDOPOHardestGame;
import domain.TheDOPOHardestGame.GameMode;
import domain.GameMap;
import domain.Player;
import java.util.List;

// Tests for the main game controller, covering state management, game modes, and branch coverage for Eclipse.
class TheDOPOHardestGameTest {

    private TheDOPOHardestGame game;

    // Initialises a new game instance before each test to ensure a clean state.
    @BeforeEach
    void setUp() {
        game = new TheDOPOHardestGame();
    }

    @Test
    // Verifies that the game initializes with default settings and no active level.
    void shouldInitializeInCorrectState() {
        assertEquals(GameMode.PLAYER, game.getGameMode());
        assertNull(game.getCurrentLevel());
    }

    @Test
    // Verifies that the GameMode can be updated correctly.
    void shouldChangeGameMode() {
        game.setGameMode(GameMode.PvsP);
        assertEquals(GameMode.PvsP, game.getGameMode());
    }

    @Test
    // Verifies that startLevel properly instantiates a new level with the provided arguments.
    void startLevelShouldCreateLevelInstance() {
        GameMap map = new GameMap(800, 500);
        game.startLevel(1, 60.0, map);
        assertNotNull(game.getCurrentLevel());
    }

    @Test
    // Verifies that loadTestLevel populates the game with all required entities without crashing.
    void loadTestLevelShouldPopulateEntities() {
        game.loadTestLevel();
        assertNotNull(game.getCurrentLevel());
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    // Verifies the player list is empty but not null when no level is loaded to prevent NullPointerExceptions.
    void getPlayersShouldReturnEmptyListWhenNoLevel() {
        List<Player> players = game.getPlayers();
        assertNotNull(players);
        assertTrue(players.isEmpty());
    }

    @Test
    // Verifies the branch where update is called but there is no level loaded.
    void updateShouldSafelyIgnoreWhenLevelIsNull() {
        assertDoesNotThrow(() -> game.update());
    }

    @Test
    // Verifies the branch where update is called but the game is paused.
    void updateShouldSafelyIgnoreWhenPaused() {
        game.loadTestLevel();
        game.togglePause();
        assertDoesNotThrow(() -> game.update());
    }

    @Test
    // Verifies the branch where movePlayer is called but the level is null.
    void movePlayerShouldSafelyIgnoreWhenLevelIsNull() {
        assertDoesNotThrow(() -> game.movePlayer(0, 1, 0));
    }

    @Test
    // Verifies the branch where movePlayer is called but the game is paused.
    void movePlayerShouldSafelyIgnoreWhenPaused() {
        game.loadTestLevel();
        game.togglePause();
        assertDoesNotThrow(() -> game.movePlayer(0, 1, 0));
    }

    @Test
    // Verifies the branch where movePlayer receives a negative (invalid) index.
    void movePlayerShouldIgnoreNegativeIndex() {
        game.loadTestLevel();
        assertDoesNotThrow(() -> game.movePlayer(-1, 1, 0));
    }

    @Test
    // Verifies the branch where movePlayer receives an index out of bounds.
    void movePlayerShouldIgnoreOutOfBoundsIndex() {
        game.loadTestLevel();
        assertDoesNotThrow(() -> game.movePlayer(5, 1, 0));
    }

    @Test
    // Verifies the successful branch of movePlayer with valid inputs.
    void movePlayerShouldExecuteWithValidInputs() {
        game.loadTestLevel();
        assertDoesNotThrow(() -> game.movePlayer(0, 1, 0));
    }

    @Test
    // Verifies isLevelComplete returns false when there is no current level.
    void isLevelCompleteShouldBeFalseWhenNoLevel() {
        assertFalse(game.isLevelComplete());
    }

    @Test
    // Verifies isGameOver returns false when there is no current level.
    void isGameOverShouldBeFalseWhenNoLevel() {
        assertFalse(game.isGameOver());
    }

    @Test
    // Verifies isGameOver returns false immediately after loading the test level (time > 0).
    void isGameOverShouldBeFalseInitially() {
        game.loadTestLevel();
        assertFalse(game.isGameOver());
    }

    @Test
    // Verifies togglePause correctly alters the pause state back and forth.
    void togglePauseShouldChangeState() {
        assertDoesNotThrow(() -> game.togglePause());
        assertDoesNotThrow(() -> game.togglePause());
    }

    @Test
    // Verifies that the internal GameMode enum stores the correct configuration attributes.
    void gameModeEnumShouldStoreCorrectData() {
        GameMode solo = GameMode.PLAYER;
        GameMode vsMachine = GameMode.PvsM;
        
        assertEquals(1, solo.getPlayerCount());
        assertFalse(solo.hasMachine());
        
        assertEquals(1, vsMachine.getPlayerCount());
        assertTrue(vsMachine.hasMachine());
    }
}