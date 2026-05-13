package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import domain.RedPlayer;
import domain.SkinCoin;

// Tests for the SkinCoin domain class, verifying that collecting a skin coin applies the correct skin to the player.
class SkinCoinTest {

    @Test
    // Verifies that collecting a blue SkinCoin changes the player's speed to BlueSkin speed.
    void onCollectShouldChangeSkin() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Blue");
        coin.onCollect(player);
        assertEquals(6.0, player.getSpeed(), 0.001);
    }

    @Test
    // Verifies that collecting a blue SkinCoin updates the player's width and height to BlueSkin size.
    void onCollectShouldChangeSkinWithCorrectSize() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Blue");
        coin.onCollect(player);
        assertEquals(30.0, player.getWidth(), 0.001);
        assertEquals(30.0, player.getHeight(), 0.001);
    }

    @Test
    // Verifies that collecting a green SkinCoin does not change the player stats (same as default).
    void onCollectGreenSkinShouldKeepDefaultStats() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Green");
        coin.onCollect(player);
        assertEquals(4.0, player.getSpeed(), 0.001);
        assertEquals(20.0, player.getWidth(), 0.001);
        assertEquals(20.0, player.getHeight(), 0.001);
    }

    @Test
    // Verifies that collecting the same SkinCoin twice only applies the skin once.
    void onCollectTwiceShouldOnlyApplyOnce() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Blue");
        coin.onCollect(player);
        player.restoreSkin();
        coin.onCollect(player);
        assertEquals(4.0, player.getSpeed(), 0.001);
    }
}
