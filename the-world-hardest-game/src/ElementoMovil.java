package domain;

public abstract class ElementoMovil {
    private int x;
    private int y;
    private double velocidad;
    private String color;

    public ElementoMovil(int x, int y, double velocidad, String color) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.color = color;
    }    

    //getters necesarios
    public int getX(){return 0;};
    public int getY(){return 0;};
    public double getVelocidad() {return 0.0;};
    public String getColor() {return null;};
    
    
    //setters necesarios
    public void setX(int x) {};
    public void setY(int y) {};
    protected void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }
}
