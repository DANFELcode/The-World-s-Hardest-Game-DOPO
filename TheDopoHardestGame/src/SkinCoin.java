package domain;

import java.awt.Color;

public class SkinCoin extends Coin {

    public SkinCoin(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    @Override
    public void onCollect(Player player) {
        player.changeSkin(getColor());
        super.onCollect(player);
    }
    
    @Override
    public Color getDisplayColor() {
        String colorName = getColor().toLowerCase();
        
        switch (colorName) {
            case "green": return Color.GREEN;
            case "blue": return Color.BLUE;
            case "red": return Color.RED;
            default: return Color.YELLOW;
        }
    }
}