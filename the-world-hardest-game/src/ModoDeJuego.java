package domain;

import java.util.ArrayList;

public abstract class ModoDeJuego {	
	protected ArrayList<Jugador> jugadores; //los tipos de modos de juego deberian poder acceder a los jugadores
	
	public ModoDeJuego() {
		this.jugadores = new ArrayList<Jugador>();
	}
	
	//control general de modos de juego
	public abstract void inicializarJugadores();
	
	//control de estados
	public abstract boolean verificarVictoria();	
	
	
}
