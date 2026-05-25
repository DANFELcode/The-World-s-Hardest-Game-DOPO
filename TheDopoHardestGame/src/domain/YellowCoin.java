package domain;

import java.awt.Color;

/**
 * Represents a standard yellow coin. Collected by the owner to progress toward level completion. <br>
 * <b>Inv:</b> inherited from Coin
 */
public class YellowCoin extends Coin {

    public YellowCoin(double x, double y, double width, double height, String color, String ownerName) {
        super(x, y, width, height, color, ownerName);
    }

    @Override
    public String getCoinType() { return "yellow"; }

    @Override
    public Color getDisplayColor() { return GameConstants.COLOR_COIN; }
}
