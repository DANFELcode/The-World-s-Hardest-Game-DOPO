package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Coin;
import domain.RedPlayer;

// Tests for the Coin domain class, verifying collection behaviour and state changes.
class CoinTest {

    private RedPlayer player;

    // Initialises a RedPlayer instance before each test.
    @BeforeEach
    void setUp() {
        player = new RedPlayer("Felipe", 100, 100);
    }

    @Test
    // Verifies that calling onCollect marks the coin as collected.
    void onCollectShouldAddCoinToPlayer() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

    @Test
    // Verifies that a coin starts uncollected and becomes collected after onCollect is called.
    void onCollectShouldMarkCoinAsCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
        coin.onCollect(player);
        assertTrue(coin.isCollected());
    }

    @Test
    // Verifies that a newly created coin is not yet collected.
    void newCoinShouldNotBeCollected() {
        Coin coin = new Coin(10, 10, 15, 15, "Yellow");
        assertFalse(coin.isCollected());
    }
}
