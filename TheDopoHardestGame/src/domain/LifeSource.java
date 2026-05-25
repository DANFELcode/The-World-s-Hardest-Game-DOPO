package domain;
import dto.DrawCommand;

import java.awt.Color;

/**
 * Represents a static collectible element that gives an extra life to the
 * owning player on contact. <br>
 * <b>(x, y, width, height, ownerName)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class LifeSource extends StaticElement implements Collectible {

    private boolean collected = false;
    private String ownerName;
    private Player ownerPlayer;

    /**
     * Creates a life source at the given position and size, owned by ownerName.
     */
    public LifeSource(double x, double y, double width, double height, String color, String ownerName) {
        super(x, y, width, height, color);
        this.ownerName = ownerName;
    }

    /** Gives an extra life only if the collecting player is the owner. */
    @Override
    public void onCollect(Player player) {
        if (!collected && player.getName().equals(ownerName)) {
            collected = true;
            player.addLife();
        }
    }

    @Override
    public void onContact(Player player, Level level) {
        onCollect(player);
    }

    @Override
    public boolean shouldBeRemoved() { return false; }

    @Override
    public boolean isVisible() { return !collected; }

    @Override
    public void reset() { collected = false; }

    @Override
    public String getOwnerName() { return ownerName; }

    @Override
    public void setOwnerPlayer(Player p) { this.ownerPlayer = p; }

    @Override
    public String getFileType() {
        return "LIFESOURCE";
    }

    @Override
    public String extraFileParams() {
        return ",owner=" + ownerName;
    }

    @Override
    public Color getDisplayColor() {
        return GameConstants.COLOR_LIFESOURCE;
    }

    @Override
    public DrawCommand toDrawCommand() {
        Color border = (ownerPlayer != null) ? ownerPlayer.getBorderColor() : Color.BLACK;
        return new DrawCommand(getDisplayColor(), (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight(),
                DrawCommand.Shape.OVAL, border);
    }
}
