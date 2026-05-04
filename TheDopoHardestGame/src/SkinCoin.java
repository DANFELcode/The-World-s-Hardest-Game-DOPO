package domain;

/**
 * Represents a skin coin in the game. When collected, temporarily changes the player's skin.
 * (x, y, width, height, color)
 * Inv: width > 0 and height > 0
 */
public class SkinCoin extends Coin {

    /**
     * Creates a skin coin at the given position, size and color.
     * @param x horizontal position
     * @param y vertical position
     * @param width coin width, must be greater than 0
     * @param height coin height, must be greater than 0
     * @param color skin color this coin represents
     */
    public SkinCoin(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Changes the player's skin to the color this coin represents.
     * @param player the player that collected the coin
     */
    @Override
    public void onCollect(Player player) {
        player.changeSkin(getColor());
        super.onCollect(player);
    }
}
