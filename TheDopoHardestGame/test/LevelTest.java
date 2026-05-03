package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Coin;
import domain.FinalZone;
import domain.GameMap;
import domain.Level;
import domain.RedPlayer;
import domain.SolidWall;

// Tests for the Level domain class, verifying completion conditions, wall detection and coin tracking.
public class LevelTest {

    private Level level;

    // Initialises a Level with a 500x500 map before each test.
    @BeforeEach
    public void setUp() {
        GameMap map = new GameMap(500, 500);
        level = new Level(1, 60.0, map);
    }

    // Verifies that the level is not complete when uncollected coins remain.
    @Test
    public void isLevelCompleteShouldReturnFalseWhenCoinsRemain() {
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        level.addCoin(coin);
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        finalZone.visit();
        level.addZone("final", finalZone);
        assertFalse(level.isLevelComplete());
    }

    // Verifies that the level is not complete when the final zone has not been visited.
    @Test
    public void isLevelCompleteShouldReturnFalseWhenFinalZoneNotVisited() {
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        level.addZone("final", finalZone);
        assertFalse(level.isLevelComplete());
    }

    // Verifies that the level is complete when all coins are collected and the final zone is visited.
    @Test
    public void isLevelCompleteShouldReturnTrueWhenAllConditionsMet() {
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        coin.onCollect(new RedPlayer("p", 0, 0));
        level.addCoin(coin);
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        finalZone.visit();
        level.addZone("final", finalZone);
        assertTrue(level.isLevelComplete());
    }

    // Verifies that isCoinsCollected returns true when no coins have been added to the level.
    @Test
    public void isCoinsCollectedShouldReturnTrueWhenNoCoinsDefined() {
        assertTrue(level.isCoinsCollected());
    }

    // Verifies that isWall returns true when a SolidWall occupies the queried position.
    @Test
    public void isWallShouldReturnTrueWhenSolidWallAtPosition() {
        SolidWall wall = new SolidWall(100, 100, 50, 50, "Gray");
        level.addStaticElement(wall);
        assertTrue(level.isWall(110, 110));
    }

    // Verifies that isWall returns false when no wall exists at the queried position.
    @Test
    public void isWallShouldReturnFalseWhenNoWallAtPosition() {
        assertFalse(level.isWall(200, 200));
    }

    // Verifies that isWalkable returns false when the position is outside the map bounds.
    @Test
    public void isWalkableShouldReturnFalseOutsideMapBounds() {
        assertFalse(level.isWalkable(-1, 0, 10, 10));
        assertFalse(level.isWalkable(495, 0, 10, 10));
    }

    // Verifies that adding a coin increases the count of uncollected coins in the level.
    @Test
    public void addCoinShouldIncreaseCoinsInLevel() {
        assertTrue(level.isCoinsCollected());
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        level.addCoin(coin);
        assertFalse(level.isCoinsCollected());
    }
}