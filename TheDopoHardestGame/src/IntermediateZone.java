package domain;

import java.awt.Color;

/**
 * Represents an intermediate checkpoint zone within the level. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class IntermediateZone extends Zone {

    @Override
    public Color getDisplayColor() { return new Color(255, 240, 150); }

    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);
        player.markCheckpoint(getX(), getY());
    }


    /**
     * Creates an intermediate zone at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width zone width, must be greater than 0
     * @param height zone height, must be greater than 0
     */
    public IntermediateZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}
