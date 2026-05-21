package presentation;

import java.awt.*;
import javax.swing.*;

/**
 * Explanation view: shows the game tutorial text and the navigation buttons
 * (VOLVER / JUGAR). Intended to grow with richer content (e.g. an animated
 * tutorial), which is why it lives in its own class.
 */
public class ExpPanel extends GradientPanel {

    private final MainView host;

    /**
     * @param host the main window, used for navigation
     */
    public ExpPanel(MainView host) {
        super(new BorderLayout());
        this.host = host;
        construirUI();
    }

    private void construirUI() {
        JLabel descripcion = new JLabel("<html><div style='text-align: justify; width: 450px'>"
            + "Eres un cuadrado. Escoge tu tipo: "
            + "<font color='red'><b>rojo</b></font> (velocidad normal), "
            + "<font color='blue'><b>azul</b></font> (1.5x mas rapido y grande) o "
            + "<font color='green'><b>verde</b></font> (absorbe el primer golpe pero pierde velocidad). "
            + "Evita los circulos <font color='#2828C8'><b>azules</b></font>: los hay normales, acelerados y patrulleros. "
            + "Recolecta todas las monedas <font color='#DAA520'><b>doradas</b></font> "
            + "y llega a la zona <font color='#3CA03C'><b>verde</b></font> antes de que se acabe el tiempo. "
            + "Las zonas <font color='#90EE90'><b>verde claro</b></font> son checkpoints: al morir respawneas ahi. "
            + "Los ovalos <font color='#FF69B4'><b>rosas</b></font> dan una vida extra. "
            + "Los ovalos <font color='#A020F0'><b>purpura</b></font> son bombas, explotan al contacto. "
            + "En modo <b>PvsP</b> dos jugadores compiten sin limite de tiempo: el primero en llegar gana."
            + "</div></html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 20));
        descripcion.setHorizontalAlignment(SwingConstants.CENTER);
        descripcion.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        panelBotones.setOpaque(false);

        JButton backInicio = UIFactory.createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        JButton playGame = UIFactory.createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        backInicio.addActionListener(e -> host.mostrarInicio());
        playGame.addActionListener(e -> host.mostrarSeleccion());

        panelBotones.add(backInicio);
        panelBotones.add(playGame);

        add(descripcion, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
}
