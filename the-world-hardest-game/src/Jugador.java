package domain;

/**
 * Representa un jugador controlable en el juego. <br>
 * <b>(nombre, muertes, monedasRecolectadas, estaVivo, estaSeguro, xInicio, yInicio)</b> <br>
 * <b>Inv:</b> muertes >= 0 and monedasRecolectadas >= 0 and xInicio >= 0 and yInicio >= 0
 */
public abstract class Jugador extends ElementoMovil {
	private String nombre;
	private int muertes;
	private int monedasRecolectadas;
	private boolean estaVivo;
	private boolean estaSeguro;
	private int xInicio;
	private int yInicio;

	/**
	 * Crea un jugador con nombre, velocidad y color definidos.
	 * @param nombre nombre del jugador
	 * @param velocidad velocidad de movimiento, debe ser mayor a 0
	 * @param color color visual del jugador
	 */
	public Jugador(String nombre, double velocidad, String color) {
	    super(0, 0, velocidad, color);
	    this.nombre = nombre;
	    this.muertes = 0;
	    this.monedasRecolectadas = 0;
	    this.estaVivo = true;
	    this.estaSeguro = false;
	}

	/**
	 * Registra la muerte del jugador y lo reubica en su punto de inicio.
	 * este metodo no contiene la logica ya que no tiene sentido que jugador
	 * sepa bajo que condiciones puede morir
	 */
	public void morir() {
		muertes += 1;
		respawn();
	}

	/**
	 * Registra la recolección de una moneda.
	 */
	public void recolectarMoneda() {
		monedasRecolectadas += 1;
	}

	/**
	 * Define el punto de reaparición del jugador.
	 * @param x coordenada horizontal del punto de inicio
	 * @param y coordenada vertical del punto de inicio
	 */
	public void setPosicionInicio(int x, int y) {
		this.xInicio = x;
		this.yInicio = y;
	}

	/**
	 * Reubica al jugador en su punto de inicio.
	 */
	public void respawn() {
		setX(xInicio);
		setY(yInicio);
	}

	/**
	 * Mueve al jugador en la dirección indicada.
	 * @param dir dirección del movimiento: 'N', 'S', 'E', 'O'
	 */
	public abstract void moverJugador(char dir);

	/**
	 * Indica si el jugador está vivo.
	 * @return true si el jugador está vivo
	 */
	public boolean estaVivo() {
		return estaVivo;
	}

	/**
	 * Indica si el jugador está en una zona segura.
	 * @return true si el jugador está en zona segura
	 */
	public boolean estaSeguro() {
		return estaSeguro;
	}

	/**
	 * Retorna el nombre del jugador.
	 * @return nombre del jugador
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Retorna el número de muertes acumuladas.
	 * @return cantidad de muertes
	 */
	public int getMuertes() {
		return muertes;
	}

	/**
	 * Retorna la cantidad de monedas recolectadas.
	 * @return monedas recolectadas
	 */
	public int getMonedasRecolectadas() {
		return monedasRecolectadas;
	}
	
	
	//setters
	
	/**
	 * Modifica estaSeguro.
	 * 
	 */
	public void setEstaSeguro(boolean estaSeguro) {
		this.estaSeguro = true;
	}
}
