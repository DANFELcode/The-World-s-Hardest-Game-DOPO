package domain;

public class LifeSource extends StaticElement implements Collectible {

    public LifeSource(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    @Override
    public void onCollect(Player player) {}
}
