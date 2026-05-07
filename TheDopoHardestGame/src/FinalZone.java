package domain;

/**
 * Represents the final zone the player must reach after collecting all coins. <br>
 * <b>(x, y, width, height, visited)</b> <br>
 * <b>Inv:</b> width > 0 and height > 0
 */
public class FinalZone extends Zone {

    public FinalZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}