package domain;

/**
 * Represents the fast player with increased speed. <br>
 * Stats are defined by BlueSkin (1.5× speed and size). <br>
 * <b>Inv:</b> speed > 0 and width > 0 and height > 0
 */
public class BluePlayer extends Player {

    public BluePlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0 * UNIT);
    }

    @Override
    protected SkinBehavior createDefaultSkin() {
        return new BlueSkin();
    }

    @Override
    public String getTypeName() { return "blue"; }
}
