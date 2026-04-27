package domain;

public abstract class Jugador extends ElementoMovil{
	private String nombre;
	private int muertes;
	private int monedasRecolectadas;	
	private boolean estaVivo;
	private boolean estaSeguro;

	
	public Jugador(String nombre) {
	    super(0, 0, 1.0, "default");
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
