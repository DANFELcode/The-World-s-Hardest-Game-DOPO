package domain;

/**
 * Represents an intermediate checkpoint zone within the level.
 * (x, y, width, height)
 * Inv: width > 0 and height > 0
 */
public class IntermediateZone extends Zone {

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
