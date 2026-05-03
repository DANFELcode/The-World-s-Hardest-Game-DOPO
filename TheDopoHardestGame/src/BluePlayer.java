package domain;

public class BluePlayer extends Player {

    public BluePlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.5);
    }

    @Override
    public void changeSkin(String newSkin) {}
}
