package domain;

/**
 * Represents the starting zone of the level. Players spawn here at the start or after dying. <br>
 * <b>(x, y, width, height)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class InitialZone extends Zone {

    /**
     * Creates an initial zone at the given position and size.
     * @param x horizontal position
     * @param y vertical position
     * @param width zone width, must be greater than 0
     * @param height zone height, must be greater than 0
     */
    public InitialZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}
