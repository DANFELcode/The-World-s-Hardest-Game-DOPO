package domain;

/**
 * Represents a collectible coin in the game. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class Coin extends StaticElement implements Collectible {

    /**
     * Creates a coin at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width coin width, must be greater than 0
     * @param height coin height, must be greater than 0
     */
    public Coin(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    /**
     * Registers the coin collection on the player.
     * @param player the player that collected the coin
     */
    public void onCollect(Player player) {
        player.collectCoin();
    }
}
