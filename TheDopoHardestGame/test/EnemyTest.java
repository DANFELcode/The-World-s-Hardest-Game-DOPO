package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.GameMap;
import domain.LinearMovement;
import domain.Direction;
import domain.MovementStrategy;
import domain.Enemy;
import domain.Level;
import domain.RedPlayer;

// Tests for the Enemy domain class, covering destruction effects and movement strategy delegation.
public class EnemyTest {

    private Level level;
    private RedPlayer player;

    // Initialises a Level and a RedPlayer instance before each test.
    @BeforeEach
    public void setUp() {
        GameMap map = new GameMap(500, 500);
        level = new Level(1, 60.0, map);
        player = new RedPlayer("Felipe", 100, 100);
    }

    // Verifies that onDestroy increments the player's death counter by one.
    @Test
    public void onDestroyShouldIncrementPlayerDeaths() {
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0,
                new LinearMovement(Direction.HORIZONTAL, 1));
        enemy.onDestroy(player);
        assertEquals(1, player.getDeaths());
    }

    // Verifies that onDestroy respawns the player at their initial spawn position.
    @Test
    public void onDestroyShouldRespawnPlayer() {
        player.setPosition(250, 300); // alejar del spawn
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0,
                new LinearMovement(Direction.HORIZONTAL, 1));
        enemy.onDestroy(player);
        assertEquals(100.0, player.getX(), 0.001);
        assertEquals(100.0, player.getY(), 0.001);
    }

    // Verifies that enemy.move delegates execution to the assigned movement strategy.
    @Test
    public void moveShouldDelegateToStrategy() {
        boolean[] called = {false};
        MovementStrategy strategy = (e, l) -> called[0] = true;
        Enemy enemy = new Enemy(0, 0, 10, 10, 1.0, strategy);
        enemy.move(level);
        assertTrue(called[0]);
    }
}