package domain;
import dto.DrawCommand;

import java.awt.Color;

/**
 * Represents a collectible coin in the game. <br>
 * <b>(x, y, width, height, collected)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class Coin extends StaticElement implements Collectible {

    private boolean collected;
    private String ownerName;
    private Player ownerPlayer;

    /**
     * Creates a coin at the given position and size, owned by ownerName.
     */
    public Coin(double x, double y, double width, double height, String color, String ownerName) {
        super(x, y, width, height, color);
        this.collected = false;
        this.ownerName = ownerName;
    }

    /**
     * Marks the coin as collected.
     * @param player the player that collected the coin
     */
    public void onCollect(Player player) {
        if (!collected && player.getName().equals(ownerName)) {
            this.collected = true;
            player.collectCoin();
        }
    }

    public String getOwnerName() { return ownerName; }

    public void setOwnerPlayer(Player p) { this.ownerPlayer = p; }

    @Override
    public void onContact(Player player, Level level) {
        onCollect(player);
    }

    /**
     * Returns whether this coin has been collected.
     * @return true if the coin has been collected
     */
    public boolean isCollected() {
        return collected;
    }

    public void reset() {
        this.collected = false;
    }

    /** Whether this coin resets on player death even when the player has a checkpoint. */
    public boolean resetsOnAnyDeath() { return false; }

    /** Returns the coin's type identifier used in level files (e.g. "yellow", "blue"). */
    public String getCoinType() { return "yellow"; }
    
    public Color getDisplayColor() {
        return GameConstants.COLOR_COIN;
    }

    @Override
    public DrawCommand toDrawCommand() {
        Color border = (ownerPlayer != null) ? ownerPlayer.getBorderColor() : null;
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), DrawCommand.Shape.OVAL, border);
    }
}