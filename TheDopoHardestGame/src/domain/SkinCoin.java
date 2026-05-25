package domain;

import java.awt.Color;

/**
 * Coin that, when collected by its owner, changes the player's active skin
 * to the type encoded in its color (e.g. "blue", "green", "red"). <br>
 * <b>Inv:</b> skinType in {"red", "blue", "green"}
 */
public class SkinCoin extends Coin {

    private final String skinType;

    public SkinCoin(double x, double y, double width, double height, String color, String ownerName) {
        super(x, y, width, height, color, ownerName);
        this.skinType = color;
    }

    @Override
    public void onCollect(Player player) {
        boolean wasUncollected = !isCollected();
        super.onCollect(player);
        if (wasUncollected && isCollected()) {
            // If the player already has this skin type, do not replace it — preserves internal
            // state such as GreenSkin's weakened flag. Only update the lastSkin record.
            if (!skinType.equals(player.getCurrentSkinType())) {
                player.restoreSkin();
                player.changeSkin(SkinBehavior.of(skinType));
            }
            player.setLastSkin(skinType);
        }
    }

    @Override
    public Color getDisplayColor() {
        return SkinBehavior.of(skinType).getDisplayColor();
    }

    @Override
    public String getCoinType() { return getColor(); }
}
