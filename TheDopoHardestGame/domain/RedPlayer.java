package domain;

public class RedPlayer extends Player {

    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0 * UNIT);
    }

    @Override
    protected SkinBehavior createDefaultSkin() {
        return new DefaultSkin();
    }

    @Override
    public String getTypeName() { return "red"; }
}
