package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.GameMap;
import domain.Level;
import domain.RedPlayer;
import domain.SolidWall;

// Tests for the RedPlayer domain class, covering movement, death, skin changes and spawn logic.
class RedPlayerTest {

    private RedPlayer player;
    private Level level;

    // Initialises a RedPlayer and a Level with a 200x200 map before each test.
    @BeforeEach
    void setUp() {
        player = new RedPlayer("Blinky", 50, 50);
        GameMap map = new GameMap(200, 200);
        level = new Level(1, 180, map);
    }

    @Test
    // Verifies Blue skin changes speed and size
    void ChangeSkinShouldChangeAtributtesForANewBlueSkin() {
        RedPlayer player = new RedPlayer("Blinky", 50, 50);
        player.changeSkin("Blue");
        assertEquals(1.5, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }

    @Test
    // Verifies that the Blue skin increases speed to 1.5 and size to 30x30
    void changeSkinBlueShouldIncreaseSpeedAndSize() {
        player.changeSkin("Blue");
        assertEquals(1.5, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }

    @Test
    // Verifies that restoreSkin resets speed and size to original values (1.0, 20x20)
    void restoreSkinShouldResetToOriginalValues() {
        player.changeSkin("Blue");
        player.restoreSkin();
        assertEquals(1.0, player.getSpeed());
        assertEquals(20.0, player.getWidth());
        assertEquals(20.0, player.getHeight());
    }

    @Test
    // Verifies that dying increments the death counter by 1
    void dieShouldIncrementDeathCounter() {
        player.die();
        assertEquals(1, player.getDeaths());
    }

    @Test
    // Verifies that dying respawns the player at the initial position (50, 50)
    void dieShouldRespawnAtInitialPosition() {
        player.move(1, 0, level);
        player.die();
        assertEquals(50.0, player.getX());
        assertEquals(50.0, player.getY());
    }

    @Test
    // Verifies that dying respawns the player at the updated spawn point
    void dieShouldRespawnAtUpdatedSpawnPoint() {
        player.setSpawnPoint(100, 100);
        player.die();
        assertEquals(100.0, player.getX());
        assertEquals(100.0, player.getY());
    }

    @Test
    // Verifies that moving on a walkable tile updates the player's position
    void moveShouldUpdatePositionWhenWalkable() {
        player.move(1, 0, level);
        assertEquals(50 + player.getSpeed(), player.getX(), 0.001);
        assertEquals(50.0, player.getY());
    }

    @Test
    // Verifies that the player cannot move into a solid wall
    void moveShouldNotMoveWhenBlockedByWall() {
        level.addStaticElement(new SolidWall(51, 50, 20, 20, "gray"));
        double xBefore = player.getX();
        player.move(1, 0, level);
        assertEquals(xBefore, player.getX());
    }

    @Test
    // Verifies that the player cannot move outside the map bounds
    void moveShouldNotMoveOutsideMapBounds() {
        RedPlayer edgePlayer = new RedPlayer("Edge", 190, 50);
        edgePlayer.move(1, 0, level);
        assertEquals(190.0, edgePlayer.getX());
    }

    @Test
    // Verifies that diagonal movement updates both X and Y axes
    void moveDiagonalShouldUpdateBothAxes() {
        player.move(1, 1, level);
        assertEquals(50 + player.getSpeed(), player.getX(), 0.001);
        assertEquals(50 + player.getSpeed(), player.getY(), 0.001);
    }

    @Test
    // Verifies that an unknown skin (Green) does not change any attributes
    void changeSkinGreenShouldNotChangeAttributes() {
        double speedBefore = player.getSpeed();
        double widthBefore = player.getWidth();
        double heightBefore = player.getHeight();
        player.changeSkin("Green");
        assertEquals(speedBefore, player.getSpeed());
        assertEquals(widthBefore, player.getWidth());
        assertEquals(heightBefore, player.getHeight());
    }

    @Test
    // Verifies that dying multiple times accumulates deaths correctly
    void multipleDeathsShouldAccumulateCorrectly() {
        player.die();
        player.die();
        player.die();
        assertEquals(3, player.getDeaths());
    }

    @Test
    // Verifies that applying the Blue skin a second time still applies the skin correctly
    void changeSkinTwiceShouldApplyLastSkin() {
        player.changeSkin("Blue");
        player.restoreSkin();
        player.changeSkin("Blue");
        assertEquals(1.5, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }
}
