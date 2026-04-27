package domain;

import java.util.ArrayList;

public class TheWorldHardestGame {
	private ArrayList<Nivel> niveles;
	private ModoDeJuego modoDeJuego;	
	private int nivelActual;
	private boolean estaPausado;
		
	
	public TheWorldHardestGame() {
		this.niveles = new ArrayList<Nivel>();
		this.modoDeJuego = null;
		this.nivelActual = 0;
		this.estaPausado = false;
	}
	
	
	//control general del juego
	
	public void iniciarJuego() {
		
	}	
	public void pausarJuego() {
		
	}
	public void terminarJuego() {
		
	}
	
	
	//control general de los niveles
	public void siguienteNivel() {
		
	}	
	public void reiniciarNivel() {		
	}
	
	//control general de los modos
	public void cambiarModoJuego() {
		
	}
	

	//control de la persistencia del juego
	
	public void guardarPartida() {
		
	}	
	public void cargarPartida() {
		
	}
	
	//control de estados
	
	public boolean estaPausado() {
		return false;
	}
	public boolean juegoTerminado(){
		return false;
	}
	

}
