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
class LevelTest {

    private Level level;

    // Initialises a Level with a 500x500 map before each test.
    @BeforeEach
    void setUp() {
        GameMap map = new GameMap(500, 500);
        level = new Level(1, 60.0, map);
    }

    @Test
    // Verifies that the level is not complete when uncollected coins remain.
    void isLevelCompleteShouldReturnFalseWhenCoinsRemain() {
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        level.addCoin(coin);
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        finalZone.visit();
        level.addZone("final", finalZone);
        assertFalse(level.isLevelComplete());
    }

    @Test
    // Verifies that the level is not complete when the final zone has not been visited.
    void isLevelCompleteShouldReturnFalseWhenFinalZoneNotVisited() {
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        level.addZone("final", finalZone);
        assertFalse(level.isLevelComplete());
    }

    @Test
    // Verifies that the level is complete when all coins are collected and the final zone is visited.
    void isLevelCompleteShouldReturnTrueWhenAllConditionsMet() {
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        coin.onCollect(new RedPlayer("p", 0, 0));
        level.addCoin(coin);
        FinalZone finalZone = new FinalZone(0, 0, 50, 50);
        finalZone.visit();
        level.addZone("final", finalZone);
        assertTrue(level.isLevelComplete());
    }

    @Test
    // Verifies that isCoinsCollected returns true when no coins have been added to the level.
    void isCoinsCollectedShouldReturnTrueWhenNoCoinsDefined() {
        assertTrue(level.isCoinsCollected());
    }

    @Test
    // Verifies that isWall returns true when a SolidWall occupies the queried position.
    void isWallShouldReturnTrueWhenSolidWallAtPosition() {
        SolidWall wall = new SolidWall(100, 100, 50, 50, "Gray");
        level.addStaticElement(wall);
        assertTrue(level.isWall(110, 110));
    }

    @Test
    // Verifies that isWall returns false when no wall exists at the queried position.
    void isWallShouldReturnFalseWhenNoWallAtPosition() {
        assertFalse(level.isWall(200, 200));
    }

    @Test
    // Verifies that isWalkable returns false when the position is outside the map bounds.
    void isWalkableShouldReturnFalseOutsideMapBounds() {
        assertFalse(level.isWalkable(-1, 0, 10, 10));
        assertFalse(level.isWalkable(495, 0, 10, 10));
    }

    @Test
    // Verifies that adding a coin increases the count of uncollected coins in the level.
    void addCoinShouldIncreaseCoinsInLevel() {
        assertTrue(level.isCoinsCollected());
        Coin coin = new Coin(10, 10, 10, 10, "Yellow");
        level.addCoin(coin);
        assertFalse(level.isCoinsCollected());
    }
}
