package domain;

import java.awt.Color;

public class SkinCoin extends Coin {

    private final SkinBehavior skin;

    public SkinCoin(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
        this.skin = buildSkin(color);
    }

    private static SkinBehavior buildSkin(String color) {
        switch (color.toLowerCase()) {
            case "blue":  return new BlueSkin();
            case "green": return new GreenSkin();
            default:      return new DefaultSkin();
        }
    }

    @Override
    public void onCollect(Player player) {
        if (!isCollected()) player.changeSkin(skin);
        super.onCollect(player);
    }

    @Override
    public Color getDisplayColor() {
        return skin.getDisplayColor();
    }

    @Override
    public String getCoinType() { return getColor(); }
}
