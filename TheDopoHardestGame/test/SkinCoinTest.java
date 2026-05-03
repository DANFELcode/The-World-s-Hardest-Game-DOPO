package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import domain.RedPlayer;
import domain.SkinCoin;

// Tests for the SkinCoin domain class, verifying that collecting a skin coin applies the correct skin to the player.
class SkinCoinTest {

    @Test
    // Verifies that collecting a SkinCoin changes the player's speed according to the skin type.
    void onCollectShouldChangeSkin() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Blue");
        coin.onCollect(player);
        assertEquals(1.5, player.getSpeed(), 0.001);
    }

    @Test
    // Verifies that collecting a SkinCoin updates the player's width and height to match the skin.
    void onCollectShouldChangeSkinWithCorrectColor() {
        RedPlayer player = new RedPlayer("Felipe", 0, 0);
        SkinCoin coin = new SkinCoin(0, 0, 10, 10, "Blue");
        coin.onCollect(player);
        assertEquals(30.0, player.getWidth(), 0.001);
        assertEquals(30.0, player.getHeight(), 0.001);
    }
}
