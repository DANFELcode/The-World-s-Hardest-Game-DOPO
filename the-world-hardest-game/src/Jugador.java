package domain;

public abstract class Jugador extends ElementoMovil{
	private String nombre;
	private int muertes;
	private int monedasRecolectadas;	
	private boolean estaVivo;
	private boolean estaSeguro;
	private int xInicio;
	private int yInicio;

	
	public Jugador(String nombre, double velocidad, String color) {
	    super(0, 0, velocidad, color);
	    this.nombre = nombre;
	    this.muertes = 0;
	    this.monedasRecolectadas = 0;
	    this.estaVivo = true;
	    this.estaSeguro = false;
	}
	
	//metodos de control general de los jugadores
	public void morir() {
		
	}
	public void recolectarMoneda() {
		
	}
	public void setPosicionInicio(int x, int y) {
		this.xInicio = x; this.yInicio = y;
		}
	public void respawn() {
		setX(xInicio); setY(yInicio);
		}
	
	//metodos de control de movimiento	
	public abstract void moverJugador(char dir);

	
	//metodos de control de estados	
	public boolean estaVivo() {
		return true;
	}	
	public boolean estaSeguro() {
		return true;
	}
	
	
	
	
	
	//getters necesarios
	public String getNombre() {return null;};
	public int getMuertes() {return 0;};
	public int getMonedasRecolectadas() {return 0;};
	
	
}
