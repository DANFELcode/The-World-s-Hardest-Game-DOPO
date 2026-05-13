package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.BlueSkin;
import domain.DefaultSkin;
import domain.GameMap;
import domain.GreenSkin;
import domain.Level;
import domain.RedPlayer;
import domain.SolidWall;

// Tests for the RedPlayer domain class, covering movement, death, skin changes and spawn logic.
class RedPlayerTest {

    private RedPlayer player;
    private Level level;

    @BeforeEach
    void setUp() {
        player = new RedPlayer("Blinky", 50, 50);
        GameMap map = new GameMap(200, 200);
        level = new Level(1, 180, map);
    }

    @Test
    // Verifies Blue skin changes speed and size
    void changeSkinShouldChangeAttributesForNewBlueSkin() {
        player.changeSkin(new BlueSkin());
        assertEquals(6.0, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }

    @Test
    // Verifies that the Blue skin increases speed to 1.5 and size to 30x30
    void changeSkinBlueShouldIncreaseSpeedAndSize() {
        player.changeSkin(new BlueSkin());
        assertEquals(6.0, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }

    @Test
    // Verifies that restoreSkin resets speed and size to original values (1.0, 20x20)
    void restoreSkinShouldResetToOriginalValues() {
        player.changeSkin(new BlueSkin());
        player.restoreSkin();
        assertEquals(4.0, player.getSpeed());
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
        player.move(1, 0, level, 1.0 / 60.0);
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
        player.move(1, 0, level, 1.0 / 60.0);
        assertEquals(50 + player.getSpeed(), player.getX(), 0.001);
        assertEquals(50.0, player.getY());
    }

    @Test
    // Verifies that the player cannot move into a solid wall
    void moveShouldNotMoveWhenBlockedByWall() {
        level.addStaticElement(new SolidWall(51, 50, 20, 20, "gray"));
        double xBefore = player.getX();
        player.move(1, 0, level, 1.0 / 60.0);
        assertEquals(xBefore, player.getX());
    }

    @Test
    // Verifies that the player cannot move outside the map bounds
    void moveShouldNotMoveOutsideMapBounds() {
        RedPlayer edgePlayer = new RedPlayer("Edge", 190, 50);
        edgePlayer.move(1, 0, level, 1.0 / 60.0);
        assertEquals(190.0, edgePlayer.getX());
    }

    @Test
    // Verifies that diagonal movement updates both X and Y axes
    void moveDiagonalShouldUpdateBothAxes() {
        player.move(1, 1, level, 1.0 / 60.0);
        assertEquals(50 + player.getSpeed(), player.getX(), 0.001);
        assertEquals(50 + player.getSpeed(), player.getY(), 0.001);
    }

    @Test
    // Verifies that Green skin applies default stats (same size and speed as default)
    void changeSkinGreenShouldApplyDefaultStats() {
        player.changeSkin(new GreenSkin());
        assertEquals(4.0, player.getSpeed());
        assertEquals(20.0, player.getWidth());
        assertEquals(20.0, player.getHeight());
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
        player.changeSkin(new BlueSkin());
        player.restoreSkin();
        player.changeSkin(new BlueSkin());
        assertEquals(6.0, player.getSpeed());
        assertEquals(30.0, player.getWidth());
        assertEquals(30.0, player.getHeight());
    }

    @Test
    // Verifies that Green skin weakens the player on first hit instead of killing
    void greenSkinShouldWeakenOnFirstHit() {
        player.changeSkin(new GreenSkin());
        double speedBefore = player.getSpeed();
        player.onHit();
        assertTrue(player.getSpeed() < speedBefore);
        assertEquals(0, player.getDeaths());
    }

    @Test
    // Verifies that Green skin kills the player on second hit
    void greenSkinShouldKillOnSecondHit() {
        player.changeSkin(new GreenSkin());
        player.onHit();
        player.onHit();
        assertEquals(1, player.getDeaths());
    }

    @Test
    // Verifies restoreSkin switches back to DefaultSkin (red color)
    void restoreSkinShouldRestoreDefaultColor() {
        player.changeSkin(new BlueSkin());
        player.restoreSkin();
        assertEquals(new DefaultSkin().getDisplayColor(), player.getDisplayColor());
    }
}
