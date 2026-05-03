package presentation;

import domain.TheDOPOHardestGame;
import java.awt.*;
import javax.swing.*;
import java.io.*;


public class TheDOPOHardestGameGUI extends JFrame {
	
	private TheDOPOHardestGame juego;
    private JMenuBar menuBar;
    private JMenu opciones, archivo;
    private JMenuItem nuevaPartida, pausar, salir, reiniciar;
    private JMenuItem guardarPartida, abrirPartida;
    
    private JPanel panel;
	private CardLayout cardLayout;
	
	
	//Panel inicio
    private JPanel panelInicio; 
    private JLabel labelTitulo;
    private JButton playGame;
    private JButton settings;
    
	//Panel Explicacion
    private JPanel panelExp; 
    private JLabel descripcion;
    private JButton playGame2;
    private JButton backInicio;

    
    //Panel Juego
    private JPanel panelJuego;
    private JPanel tablero;
    private JButton menu;
    private JLabel niveles;
    private JLabel muertes;
    private JLabel tiempo;
    
    
    
    public TheDOPOHardestGameGUI() {
    	super("TheDOPOHardestGame");
    	juego = new TheDOPOHardestGame();
    	
        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);
    	
        prepareElements();
        prepareActions();
    	
    }
    
    public static void main(String[] args) {
        TheDOPOHardestGame ventana = new TheDOPOHardestGameGUI();
    }

    

    private void prepareActions() {
		// TODO Auto-generated method stub
		
	}

	public void prepareElements() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width / 2, screenSize.height / 2); 
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());


        prepareElementsPanelInicio();
        prepareElementsPanelExp();
        prepareElementsPanelJuego();
        

       
    }

	private void prepareElementsPanelJuego() {
		// TODO Auto-generated method stub
		
	}

	private void prepareElementsPanelExp() {
		// TODO Auto-generated method stub
		
	}

	private void prepareElementsPanelInicio() {
        panelInicio = new JPanel(cardLayout);
		
        labelTitulo = new JLabel(" The DOPO Hardest Game");
        playGame = new JButton("PLAY GAME");
        settings = new JButton("CONFIGURACIÓN");
        
		this.add(labelTitulo, BorderLayout.CENTER);
		this.add(playGame, BorderLayout.CENTER);
		this.add(settings, BorderLayout.CENTER);
	}
    
   

    



   
    

	
	
}
