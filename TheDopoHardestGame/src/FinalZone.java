package domain;

/**
 * Represents the final zone the player must reach after collecting all coins. 
 * (x, y, width, height, visited)
 * Inv: width > 0 and height > 0
 */
public class FinalZone extends Zone {

    public FinalZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}