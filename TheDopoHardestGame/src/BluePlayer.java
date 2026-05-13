package domain;

import java.awt.Color;

/**
 * Represents the fast player with increased speed. <br>
 * <b>(name, x, y, width=20.0, height=20.0, speed=1.5)</b> <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class BluePlayer extends Player {

    public BluePlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.5 * UNIT);
    }

    @Override
    public Color getDisplayColor() { return Color.BLUE; }

    @Override
    public String getTypeName() { return "blue"; }
}
