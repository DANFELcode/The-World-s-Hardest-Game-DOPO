package domain;

public class RedPlayer extends Player {

    public RedPlayer(String name, double x, double y) {
        super(name, x, y, 20.0, 20.0, 1.0 * UNIT);
    }

    //el tipo de retorno declarado es la interfaz pero el objeto que retorna puede ser cualquier clase que la implemente
    @Override
    protected SkinBehavior createDefaultSkin() {
        return new DefaultSkin();
    }

    @Override
    public String getTypeName() { return "red"; }
}
