package domain;

public class Bomb extends StaticElement implements Lethal {

    public Bomb(double x, double y, double width, double height, String color) {
        super(x, y, width, height, color);
    }

    @Override
    public void onDestroy(Player player) {}
}
