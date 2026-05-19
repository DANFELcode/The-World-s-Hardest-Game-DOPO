package domain;

import java.awt.Color;

/**
 * Represents an intermediate checkpoint zone within the level. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class IntermediateZone extends Zone {

    public IntermediateZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Color getDisplayColor() {
        return GameConstants.COLOR_INTERMEDIATE_ZONE;
    }

    /**
     * Marks the zone as visited and updates the player's checkpoint.
     * @param player the player that reached the checkpoint
     */
    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);

        double centerX = this.x + (this.width / 2.0);
        double centerY = this.y + (this.height / 2.0);
        player.markCheckpoint(centerX, centerY);
    }
}
