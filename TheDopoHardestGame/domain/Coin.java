package domain;
import dto.DrawCommand;

import java.awt.Color;

/**
 * Represents a collectible coin in the game. <br>
 * <b>(x, y, width, height, collected)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public abstract class Coin extends StaticElement implements Collectible {

    private boolean collected;
    private boolean protectedByCheckpoint;
    private String ownerName;
    private Player ownerPlayer;

    /**
     * Creates a coin at the given position and size, owned by ownerName.
     */
    public Coin(double x, double y, double width, double height, String color, String ownerName) {
        super(x, y, width, height, color);
        this.collected = false;
        this.protectedByCheckpoint = false;
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
            applyEffect(player);
        }
    }

    // Efecto extra al recoger, vacío por defecto. Las subclases lo redefinen.
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void applyEffect(Player player) { }

    @Override
    public String getOwnerName() { return ownerName; }

    @Override
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
        this.protectedByCheckpoint = false;
    }

    /**
     * Marks this coin as protected by a checkpoint. Protected coins are not reset on death,
     * unless {@link #resetsOnAnyDeath()} returns true.
     */
    public void protectByCheckpoint() {
        this.protectedByCheckpoint = true;
    }

    /**
     * @return true if this coin was collected before the player's checkpoint was reached
     */
    public boolean isProtectedByCheckpoint() {
        return protectedByCheckpoint;
    }

    /** Whether this coin resets on player death even when the player has a checkpoint. */
    public boolean resetsOnAnyDeath() { return false; }

    /** Returns the coin's type identifier used in level files (e.g. "yellow", "blue"). */
    public abstract String getCoinType();

    public abstract Color getDisplayColor();

    @Override
    public DrawCommand toDrawCommand() {
        Color border = (ownerPlayer != null) ? ownerPlayer.getBorderColor() : null;
        return new DrawCommand(getDisplayColor(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), DrawCommand.Shape.OVAL, border);
    }
}
