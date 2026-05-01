package domain;

import java.util.ArrayList;

public class Nivel {
	private static final int TIEMPO_LIMITE = 180; //3 minutos
	
	private int numero;		
	private Mapa mapa;	
	
	private ArrayList<Enemigo> enemigos;
	private ArrayList<Objetivo> objetivos;
	private ArrayList<Obstaculo> obstaculos;
	
	public Nivel(int numero) {
	    this.numero = numero;	    
	    this.mapa = null;
	    this.enemigos = new ArrayList<Enemigo>();
	    this.objetivos = new ArrayList<Objetivo>();
	    this.obstaculos = new ArrayList<Obstaculo>();
	}
	
	//metodos de control general de nivel	
	public void iniciarNivel() {
		
	}	
	public void actualizarNivel() {
		
	}
	
	//metodos de control de estados	
	public boolean nivelCompletado() {
		return false;
	}
	
	//metodos de control de entidades
    public void agregarEnemigo(Enemigo e) {};
    public void agregarObjetivo(Objetivo o) {};
    public void agregarObstaculo(Obstaculo ob) {};
    
    
	//getters necesarios
	public int getNumero() {return 0;}
	public Mapa getMapa() {return mapa;}
	public ArrayList<Colisionable> getColisionables(){return null;};
	
	//setters necesarios
	public void setMapa(Mapa mapa) {};
	
	
	
	

	
	
	
	
	
	
}
