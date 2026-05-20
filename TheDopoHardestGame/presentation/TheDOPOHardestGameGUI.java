package presentation;

import domain.TheDOPOHardestGame;
import domain.GameMode;
import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.*;

public class TheDOPOHardestGameGUI extends JFrame {

    private TheDOPOHardestGame juego;
    private JMenuBar menuBar;
    private JMenu opciones, archivo;
    private JMenuItem nuevaPartida, pausar, salir, reiniciar;
    private JMenuItem guardarPartida, abrirPartida, exportarNivel, importarNivel;

    private JPanel panel;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private JButton playGame;
    private JButton settings;

    private JPanel panelExp;
    private JLabel descripcion;
    private JButton playGame2;
    private JButton backInicio;

    private JPanel panelSeleccion;
    private JButton btnModePlayer, btnModePvsP, btnModePvsM;
    private JButton btnSkinRojo, btnSkinAzul, btnSkinVerde;
    private JPanel filaBorde1, filaBorde2;
    private JLabel labelBorde2;
    private JButton[] borde1Botones;
    private JButton[] borde2Botones;
    private JButton btnJugarSeleccion, btnVolverSeleccion;
    private JPanel filaLevel;
    private PlayerPreviewPanel playerPreview;
    private int selectedLevel = 1;
    private GameMode selectedMode = GameMode.PLAYER;
    private String selectedSkin = "red";
    private Color selectedBorder1 = Color.BLACK;
    private Color selectedBorder2 = Color.WHITE;

    //dominio lista de colores que se puedan escoger y personalizar, obtener border names desde dominio
    private static final String[] BORDER_NAMES = {"NEGRO", "BLANCO", "AMARILLO", "CYAN", "MAGENTA"};
    private static final Color[] BORDER_COLORS = {Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    private JPanel panelJuego;
    private BoardPanel tablero;
    private JButton menu;
    private JLabel niveles;
    private JLabel muertes;
    private JLabel monedas;
    private JLabel tiempo;

    private GameLoop gameLoop;

    private final Set<Integer> keysDownPlayer1 = new HashSet<>();
    private final Set<Integer> keysDownPlayer2 = new HashSet<>();
    

    private static final Color COLOR_FONDO        = new Color(180, 180, 220);
    private static final String PANEL_INICIO      = "inicio";
    private static final String PANEL_EXPLICACION = "explicacion";
    private static final String PANEL_SELECCION   = "seleccion";
    private static final String PANEL_JUEGO       = "juego";

    public TheDOPOHardestGameGUI() {
        super("TheDOPOHardestGame");
        juego = new TheDOPOHardestGame();

        prepareElements();
        prepareActions();
        gameLoop = new GameLoop(this);

        pack();
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        TheDOPOHardestGameGUI ventana = new TheDOPOHardestGameGUI();
        ventana.setVisible(true);
    }

    public void prepareElements() {
        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);
        
        prepareElementsMenuBar();
        prepareElementsPanelInicio();
        prepareElementsPanelExp();
        prepareElementsPanelSeleccion();
        prepareElementsPanelJuego();

        this.add(panel);
    }

    private void prepareElementsMenuBar() {
        menuBar = new JMenuBar();

        archivo = new JMenu("Archivo");
        guardarPartida = new JMenuItem("Guardar Partida");
        abrirPartida = new JMenuItem("Abrir Partida");
        exportarNivel = new JMenuItem("Exportar Nivel");
        importarNivel = new JMenuItem("Importar Nivel");
        archivo.add(guardarPartida);
        archivo.add(abrirPartida);
        archivo.addSeparator();
        archivo.add(exportarNivel);
        archivo.add(importarNivel);

        opciones = new JMenu("Opciones");
        nuevaPartida = new JMenuItem("Nueva Partida");
        pausar = new JMenuItem("Pausar");
        reiniciar = new JMenuItem("Reiniciar");
        salir = new JMenuItem("Salir");
        opciones.add(nuevaPartida);
        opciones.add(pausar);
        opciones.add(reiniciar);
        opciones.addSeparator();
        opciones.add(salir);

        menuBar.add(archivo);
        menuBar.add(opciones);

        this.setJMenuBar(menuBar);
    }

    private JButton createStyledButton(String text, Color fillColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth(), h = getHeight();

                // Fondo redondeado sutil cuando hover
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.fillRoundRect(0, 0, w, h, 24, 24);
                }

                // Arial Black (más grueso que Arial Bold)
                Font font = new Font("Arial Black", Font.BOLD, 42);
                java.awt.font.FontRenderContext frc = g2.getFontRenderContext();
                java.awt.font.TextLayout layout = new java.awt.font.TextLayout(getText(), font, frc);
                java.awt.geom.Rectangle2D bounds = layout.getBounds();

                // Centrado horizontal por bounds del texto
                double x = w / 2.0 - bounds.getCenterX();
                // Centrado vertical por métricas del font (consistente con o sin acentos)
                java.awt.font.LineMetrics lm = font.getLineMetrics("Ay", frc);
                double y = h / 2.0 + (lm.getAscent() - lm.getDescent()) / 2.0;

                java.awt.geom.AffineTransform transform = java.awt.geom.AffineTransform.getTranslateInstance(x, y);
                java.awt.Shape shape = layout.getOutline(transform);

                // Extrusión 3D: copias offset hacia abajo-derecha en versión oscura del color
                Color deep = fillColor.darker().darker();
                int depth = 3;
                for (int i = depth; i >= 1; i--) {
                    java.awt.geom.AffineTransform extrudeTr =
                        java.awt.geom.AffineTransform.getTranslateInstance(x + i * 0.7, y + i * 0.7);
                    java.awt.Shape extrudeShape = layout.getOutline(extrudeTr);
                    float t = i / (float) depth;
                    int r = (int) (fillColor.getRed()   * (1 - t) * 0.4 + deep.getRed()   * t * 0.6);
                    int gg = (int) (fillColor.getGreen() * (1 - t) * 0.4 + deep.getGreen() * t * 0.6);
                    int b = (int) (fillColor.getBlue()  * (1 - t) * 0.4 + deep.getBlue()  * t * 0.6);
                    g2.setColor(new Color(Math.min(255, r), Math.min(255, gg), Math.min(255, b)));
                    g2.fill(extrudeShape);
                }

                // Gradiente vertical del fill (más claro arriba, más oscuro abajo)
                Color top = getModel().isPressed() ? fillColor.darker()
                          : getModel().isRollover() ? brighten(fillColor, 0.25f)
                          : brighten(fillColor, 0.15f);
                Color bot = getModel().isPressed() ? fillColor.darker().darker()
                          : fillColor;

                java.awt.geom.Rectangle2D sb = shape.getBounds2D();
                GradientPaint grad = new GradientPaint(
                    0, (float) sb.getMinY(), top,
                    0, (float) sb.getMaxY(), bot);
                g2.setPaint(grad);
                g2.fill(shape);

                // Outline negro delgado
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(shape);

                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        // Calcular ancho del botón según el texto real + padding lateral
        Font measureFont = new Font("Arial Black", Font.BOLD, 42);
        java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, true, true);
        java.awt.font.TextLayout layout = new java.awt.font.TextLayout(text, measureFont, frc);
        int textWidth = (int) Math.ceil(layout.getBounds().getWidth());
        btn.setPreferredSize(new Dimension(textWidth + 60, 90));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createGradientPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint bg = new GradientPaint(
                    0, 0, new Color(213, 213, 255),
                    0, getHeight(), Color.WHITE);
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private static Color brighten(Color c, float amount) {
        int r = Math.min(255, (int) (c.getRed()   + (255 - c.getRed())   * amount));
        int g = Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * amount));
        int b = Math.min(255, (int) (c.getBlue()  + (255 - c.getBlue())  * amount));
        return new Color(r, g, b);
    }

    private void prepareElementsPanelInicio() {
        panelInicio = createGradientPanel(new BorderLayout());

        JPanel grupoArriba = new JPanel();
        grupoArriba.setLayout(new BoxLayout(grupoArriba, BoxLayout.Y_AXIS));
        grupoArriba.setOpaque(false);

        JComponent tituloDibujado = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();

                // Calcular primero la posición y ancho del título grande
                Font bigFont = new Font("Impact", Font.PLAIN, 95);
                java.awt.font.FontRenderContext frc = g2.getFontRenderContext();
                String big = "HARDEST GAME";
                java.awt.font.TextLayout layout = new java.awt.font.TextLayout(big, bigFont, frc);

                double baseW = layout.getBounds().getWidth();
                double maxW = w * 0.92;
                double scaleX = Math.min(1.8, maxW / baseW);
                double textW = baseW * scaleX;
                double bigX = (w - textW) / 2.0;
                double bigY = 175;

                // Línea pequeña: alineada al borde izquierdo del título grande
                Font smallFont = new Font("Arial", Font.BOLD, 32);
                g2.setFont(smallFont);
                g2.setColor(Color.BLACK);
                String small = "THE DOPO...";
                g2.drawString(small, (int) bigX, 75);

                java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
                transform.translate(bigX, bigY);
                transform.scale(scaleX, 1.0);
                java.awt.Shape outline = layout.getOutline(transform);

                java.awt.geom.Rectangle2D shapeBounds = outline.getBounds2D();
                float minY = (float) shapeBounds.getMinY();
                float maxY = (float) shapeBounds.getMaxY();

                // Extrusión 3D: copias offset hacia abajo-derecha en azul oscuro
                int depth = 10;
                for (int i = depth; i >= 1; i--) {
                    java.awt.geom.AffineTransform extrudeTr =
                        java.awt.geom.AffineTransform.getTranslateInstance(i * 0.8, i * 0.8);
                    java.awt.Shape extrudeShape = extrudeTr.createTransformedShape(outline);
                    float t = i / (float) depth;
                    int r = (int) (30 + (10 - 30) * (1 - t));
                    int g3 = (int) (50 + (25 - 50) * (1 - t));
                    int b = (int) (100 + (60 - 100) * (1 - t));
                    g2.setColor(new Color(r, g3, b));
                    g2.fill(extrudeShape);
                }

                // Doble contorno alrededor de la cara frontal: negro grueso + blanco encima
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(outline);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(outline);

                // Gradiente glossy: azul grisáceo débil arriba → azul medio → más oscuro abajo
                java.awt.LinearGradientPaint gradient = new java.awt.LinearGradientPaint(
                    0, minY, 0, maxY,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{
                        new Color(0x9A, 0xB1, 0xCC),
                        new Color(0x6E, 0xA0, 0xE6),
                        new Color(0x46, 0x79, 0xBE)
                    }
                );
                g2.setPaint(gradient);
                g2.fill(outline);

                // Brillo superior sutil
                java.awt.geom.Rectangle2D sheenRect = new java.awt.geom.Rectangle2D.Double(
                    shapeBounds.getX(), minY, shapeBounds.getWidth(),
                    shapeBounds.getHeight() * 0.45);
                java.awt.Shape sheenClip = new java.awt.geom.Area(outline);
                ((java.awt.geom.Area) sheenClip).intersect(new java.awt.geom.Area(sheenRect));
                LinearGradientPaint sheen = new LinearGradientPaint(
                    0, minY, 0, (float)(minY + shapeBounds.getHeight() * 0.45),
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(255, 255, 255, 120),
                        new Color(255, 255, 255, 0)
                    }
                );
                g2.setPaint(sheen);
                g2.fill(sheenClip);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 220);
            }
        };
        tituloDibujado.setOpaque(false);
        tituloDibujado.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);

        playGame = createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        settings = createStyledButton("CONFIGURACIÓN", new Color(0x4C, 0xC1, 0x6D));

        panelBotones.add(playGame);
        panelBotones.add(settings);

        grupoArriba.add(Box.createVerticalStrut(40));
        grupoArriba.add(tituloDibujado);
        grupoArriba.add(Box.createVerticalStrut(40));
        grupoArriba.add(panelBotones);

        panelInicio.add(grupoArriba, BorderLayout.CENTER);

        panel.add(panelInicio, PANEL_INICIO);
    }

    private void prepareElementsPanelExp() {
        panelExp = createGradientPanel(new BorderLayout());

        descripcion = new JLabel("<html><div style='text-align: justify; width: 450px'>"
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

        backInicio = createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        playGame2 = createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));

        panelBotones.add(backInicio);
        panelBotones.add(playGame2);

        panelExp.add(descripcion, BorderLayout.CENTER);
        panelExp.add(panelBotones, BorderLayout.SOUTH);

        panel.add(panelExp, PANEL_EXPLICACION);
    }

    private void prepareElementsPanelSeleccion() {
        panelSeleccion = createGradientPanel(new BorderLayout());

        JLabel titulo = new JLabel("Seleccione el modo de juego");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 80, 180));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panelSeleccion.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(5, 1, 0, 10));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        // Fila de modos
        JPanel filaModos = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filaModos.setOpaque(false);
        JLabel labelModo = new JLabel("Modo:  ");
        labelModo.setFont(new Font("Arial", Font.BOLD, 16));
        btnModePlayer = new JButton("PLAYER");
        btnModePvsP   = new JButton("PvsP");
        btnModePvsM   = new JButton("PvsM");
        for (JButton b : new JButton[]{btnModePlayer, btnModePvsP, btnModePvsM}) {
            b.setFont(new Font("Arial", Font.BOLD, 16));
        }
        filaModos.add(labelModo);
        filaModos.add(btnModePlayer);
        filaModos.add(btnModePvsP);
        filaModos.add(btnModePvsM);

        // Fila de skins
        JPanel filaSkins = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filaSkins.setOpaque(false);
        JLabel labelSkin = new JLabel("Skin:  ");
        labelSkin.setFont(new Font("Arial", Font.BOLD, 16));
        btnSkinRojo  = new JButton("ROJO");
        btnSkinAzul  = new JButton("AZUL");
        btnSkinVerde = new JButton("VERDE");
        btnSkinRojo.setForeground(Color.RED);
        btnSkinAzul.setForeground(Color.BLUE);
        btnSkinVerde.setForeground(new Color(0, 150, 0));
        for (JButton b : new JButton[]{btnSkinRojo, btnSkinAzul, btnSkinVerde}) {
            b.setFont(new Font("Arial", Font.BOLD, 16));
        }
        filaSkins.add(labelSkin);
        filaSkins.add(btnSkinRojo);
        filaSkins.add(btnSkinAzul);
        filaSkins.add(btnSkinVerde);

        // Fila de borde P1
        filaBorde1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaBorde1.setOpaque(false);
        JLabel labelBorde1 = new JLabel("Borde P1:  ");
        labelBorde1.setFont(new Font("Arial", Font.BOLD, 16));
        filaBorde1.add(labelBorde1);
        borde1Botones = new JButton[BORDER_NAMES.length];
        for (int i = 0; i < BORDER_NAMES.length; i++) {
            borde1Botones[i] = new JButton(BORDER_NAMES[i]);
            borde1Botones[i].setFont(new Font("Arial", Font.BOLD, 14));
            filaBorde1.add(borde1Botones[i]);
        }

        // Fila de borde P2 (visible solo en PvsP/PvsM)
        filaBorde2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaBorde2.setOpaque(false);
        labelBorde2 = new JLabel("Borde P2:  ");
        labelBorde2.setFont(new Font("Arial", Font.BOLD, 16));
        filaBorde2.add(labelBorde2);
        borde2Botones = new JButton[BORDER_NAMES.length];
        for (int i = 0; i < BORDER_NAMES.length; i++) {
            borde2Botones[i] = new JButton(BORDER_NAMES[i]);
            borde2Botones[i].setFont(new Font("Arial", Font.BOLD, 14));
            filaBorde2.add(borde2Botones[i]);
        }
        filaBorde2.setVisible(false);

        // Fila de niveles
        filaLevel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filaLevel.setOpaque(false);
        JLabel labelLevel = new JLabel("Nivel:  ");
        labelLevel.setFont(new Font("Arial", Font.BOLD, 16));
        filaLevel.add(labelLevel);

        centro.add(filaModos);
        centro.add(filaSkins);
        centro.add(filaBorde1);
        centro.add(filaBorde2);
        centro.add(filaLevel);

        // Preview panel a la derecha
        playerPreview = new PlayerPreviewPanel();
        JPanel previewWrapper = new JPanel(new BorderLayout());
        previewWrapper.setOpaque(false);
        previewWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 40));
        JLabel previewLabel = new JLabel("Tu personaje", SwingConstants.CENTER);
        previewLabel.setFont(new Font("Arial", Font.BOLD, 14));
        previewLabel.setForeground(new Color(30, 80, 180));
        previewWrapper.add(previewLabel, BorderLayout.NORTH);
        previewWrapper.add(playerPreview, BorderLayout.CENTER);

        JPanel split = new JPanel(new BorderLayout());
        split.setOpaque(false);
        split.add(centro, BorderLayout.CENTER);
        split.add(previewWrapper, BorderLayout.EAST);

        panelSeleccion.add(split, BorderLayout.CENTER);

        // Botones de navegación
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        filaBotones.setOpaque(false);
        btnVolverSeleccion = createStyledButton("VOLVER", new Color(0x9A, 0x4B, 0xC1));
        btnJugarSeleccion = createStyledButton("JUGAR", new Color(0xEB, 0x55, 0x55));
        filaBotones.add(btnVolverSeleccion);
        filaBotones.add(btnJugarSeleccion);
        panelSeleccion.add(filaBotones, BorderLayout.SOUTH);

        panel.add(panelSeleccion, PANEL_SELECCION);
    }

    private void prepareElementsPanelJuego() {
        panelJuego = new JPanel(new BorderLayout());

        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Color.BLACK);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        menu = new JButton("MENU");
        menu.setForeground(Color.WHITE);
        menu.setBackground(Color.BLACK);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        menu.setBorderPainted(false);

        niveles = new JLabel("Nivel: 1/2");
        niveles.setForeground(Color.WHITE);
        niveles.setFont(new Font("Arial", Font.BOLD, 14));

        monedas = new JLabel("Monedas: 0/0");
        monedas.setForeground(Color.WHITE);
        monedas.setFont(new Font("Arial", Font.BOLD, 14));

        muertes = new JLabel("MUERTES: 0");
        muertes.setForeground(Color.WHITE);
        muertes.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        centerInfo.setBackground(Color.BLACK);
        centerInfo.add(niveles);
        centerInfo.add(monedas);

        panelInfo.add(menu, BorderLayout.WEST);
        panelInfo.add(centerInfo, BorderLayout.CENTER);
        panelInfo.add(muertes, BorderLayout.EAST);

        tablero = new BoardPanel();
        tablero.setGame(juego);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Color.BLACK);
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelSuaMunar = new JLabel("SUA-MUNAR");
        labelSuaMunar.setForeground(Color.WHITE);
        labelSuaMunar.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(labelSuaMunar, BorderLayout.WEST);

        tiempo = new JLabel("Tiempo: 60");
        tiempo.setForeground(Color.WHITE);
        tiempo.setFont(new Font("Arial", Font.BOLD, 14));
        tiempo.setHorizontalAlignment(SwingConstants.CENTER);
        panelSur.add(tiempo, BorderLayout.CENTER);

        panelJuego.add(panelInfo, BorderLayout.NORTH);
        panelJuego.add(tablero, BorderLayout.CENTER);
        panelJuego.add(panelSur, BorderLayout.SOUTH);

        panel.add(panelJuego, PANEL_JUEGO);
    }

    private void prepareActions() {
        prepareActionsWindow();
        prepareActionsPanelInicio();
        prepareActionsPanelExp();
        prepareActionsPanelSeleccion();
        prepareActionsMenuArchivo();
        prepareActionsMenuOpciones();
        prepareActionsPanelJuego();
    }

    private void prepareActionsWindow() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });
    }

    private void prepareActionsPanelInicio() {
        playGame.addActionListener(e -> cardLayout.show(panel, PANEL_EXPLICACION));
    }

    private void prepareActionsPanelExp() {
        backInicio.addActionListener(e -> cardLayout.show(panel, PANEL_INICIO));
        playGame2.addActionListener(e -> cardLayout.show(panel, PANEL_SELECCION));
    }

    private void prepareActionsPanelSeleccion() {
        btnVolverSeleccion.addActionListener(e -> cardLayout.show(panel, PANEL_EXPLICACION));

        highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
        highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
        refreshBorderButtonStates();

        btnModePlayer.addActionListener(e -> {
            selectedMode = GameMode.PLAYER;
            selectedLevel = 1;
            highlightButton(btnModePlayer, btnModePvsP, btnModePvsM);
            filaBorde2.setVisible(false);
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsP.addActionListener(e -> {
            selectedMode = GameMode.PvsP;
            selectedLevel = 1;
            highlightButton(btnModePvsP, btnModePlayer, btnModePvsM);
            filaBorde2.setVisible(true);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });
        btnModePvsM.addActionListener(e -> {
            selectedMode = GameMode.PvsM;
            selectedLevel = 1;
            highlightButton(btnModePvsM, btnModePlayer, btnModePvsP);
            filaBorde2.setVisible(true);
            ensureBordersDiffer();
            refreshBorderButtonStates();
            refreshLevelButtons();
            updatePreview();
        });

        for (int i = 0; i < BORDER_NAMES.length; i++) {
            final int idx = i;
            borde1Botones[i].addActionListener(e -> {
                selectedBorder1 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
                    }
                }
                refreshBorderButtonStates();
                updatePreview();
            });
            borde2Botones[i].addActionListener(e -> {
                selectedBorder2 = BORDER_COLORS[idx];
                if (selectedBorder1.equals(selectedBorder2)) {
                    for (Color c : BORDER_COLORS) {
                        if (!c.equals(selectedBorder2)) { selectedBorder1 = c; break; }
                    }
                }
                refreshBorderButtonStates();
                updatePreview();
            });
        }

        btnSkinRojo.addActionListener(e -> {
            selectedSkin = "red";
            highlightButton(btnSkinRojo, btnSkinAzul, btnSkinVerde);
            updatePreview();
        });
        btnSkinAzul.addActionListener(e -> {
            selectedSkin = "blue";
            highlightButton(btnSkinAzul, btnSkinRojo, btnSkinVerde);
            updatePreview();
        });
        btnSkinVerde.addActionListener(e -> {
            selectedSkin = "green";
            highlightButton(btnSkinVerde, btnSkinRojo, btnSkinAzul);
            updatePreview();
        });

        btnJugarSeleccion.addActionListener(e -> {
            juego.setGameMode(selectedMode);
            juego.setPlayerType("Player1", selectedSkin);
            juego.setPlayerBorderColor("Player1", selectedBorder1);
            if (selectedMode == GameMode.PvsP || selectedMode == GameMode.PvsM) {
                juego.setPlayerType("Player2", selectedSkin);
                juego.setPlayerBorderColor("Player2", selectedBorder2);
            }
            juego.startGame(selectedLevel);
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });

        refreshLevelButtons();
        updatePreview();
    }

    private void updatePreview() {
        boolean two = selectedMode == GameMode.PvsP || selectedMode == GameMode.PvsM;
        boolean isMachine = selectedMode == GameMode.PvsM;
        playerPreview.setSkin1(selectedSkin);
        playerPreview.setBorder1(selectedBorder1);
        playerPreview.setSkin2(selectedSkin);
        playerPreview.setBorder2(selectedBorder2);
        playerPreview.setShowTwo(two);
        playerPreview.setLabel2(isMachine ? "MAQUINA" : "P2");
        labelBorde2.setText(isMachine ? "Borde Maquina:  " : "Borde P2:  ");
    }

    private void refreshLevelButtons() {
        // Remove old level buttons (keep the label at index 0)
        while (filaLevel.getComponentCount() > 1) filaLevel.remove(1);
        juego.setGameMode(selectedMode);
        int count = juego.getAvailableLevelCount();
        for (int i = 1; i <= count; i++) {
            final int lvl = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            if (lvl == selectedLevel) btn.setBackground(new Color(0xEB, 0x55, 0x55));
            btn.addActionListener(e -> {
                selectedLevel = lvl;
                refreshLevelButtons();
            });
            filaLevel.add(btn);
        }
        if (selectedLevel > count) selectedLevel = 1;
        filaLevel.revalidate();
        filaLevel.repaint();
    }

    private void ensureBordersDiffer() {
        if (selectedBorder1.equals(selectedBorder2)) {
            for (Color c : BORDER_COLORS) {
                if (!c.equals(selectedBorder1)) { selectedBorder2 = c; break; }
            }
        }
    }

    private void prepareActionsMenuArchivo() {
        guardarPartida.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.guardarPartida(fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Partida guardada exitosamente.");
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        abrirPartida.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.abrirPartida(fc.getSelectedFile());
                    cardLayout.show(panel, PANEL_JUEGO);
                    SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                    gameLoop.start();
                    JOptionPane.showMessageDialog(this, "Partida cargada exitosamente.");
                    return;
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        exportarNivel.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser(new java.io.File("resources/levels/user"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.exportarNivel(fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Nivel exportado exitosamente.");
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });

        importarNivel.addActionListener(e -> {
            gameLoop.stop();
            JFileChooser fc = new JFileChooser(new java.io.File("resources/levels/user"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    juego.importarNivel(fc.getSelectedFile());
                    cardLayout.show(panel, PANEL_JUEGO);
                    SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                    gameLoop.start();
                    JOptionPane.showMessageDialog(this, "Nivel importado exitosamente.");
                    return;
                } catch (domain.GameException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (panelJuego.isShowing()) gameLoop.start();
        });
    }

    private void prepareActionsMenuOpciones() {
        salir.addActionListener(e -> exit());

        nuevaPartida.addActionListener(e -> {
            gameLoop.stop();
            int confirm = JOptionPane.showConfirmDialog(this, "¿Iniciar una nueva partida?",
                "Nueva Partida", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                keysDownPlayer1.clear();
                keysDownPlayer2.clear();
                juego.startGame(1);
                cardLayout.show(panel, PANEL_JUEGO);
                SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
                gameLoop.start();
            } else if (panelJuego.isShowing()) {
                gameLoop.start();
            }
        });

        pausar.addActionListener(e -> {
            juego.togglePause();
            pausar.setText(juego.isPaused() ? "Reanudar" : "Pausar");
        });

        reiniciar.addActionListener(e -> {
            gameLoop.stop();
            keysDownPlayer1.clear();
            keysDownPlayer2.clear();
            juego.restartLevel();
            cardLayout.show(panel, PANEL_JUEGO);
            SwingUtilities.invokeLater(() -> tablero.requestFocusInWindow());
            gameLoop.start();
        });
    }

    private void prepareActionsPanelJuego() {
        menu.addActionListener(e -> {
            gameLoop.stop();
            cardLayout.show(panel, PANEL_INICIO);
        });

        tablero.setFocusable(true);
        tablero.addKeyListener(new KeyAdapter() {
            private static final Set<Integer> PVP_KEYS = new HashSet<>(java.util.Arrays.asList(
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
            ));

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    juego.togglePause();
                    pausar.setText(juego.isPaused() ? "Reanudar" : "Pausar");
                    return;
                }
                if (PVP_KEYS.contains(e.getKeyCode())) {
                    keysDownPlayer2.add(e.getKeyCode());
                } else {
                    keysDownPlayer1.add(e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysDownPlayer2.remove(e.getKeyCode());
                keysDownPlayer1.remove(e.getKeyCode());
            }
        });
    }

    public void update() {
    	updatePlayer(0, keysDownPlayer1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
    	if (juego.getGameMode() == GameMode.PvsP)
    	    updatePlayer(1, keysDownPlayer2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        juego.update();
    }
    
    private void updatePlayer(int index, Set<Integer> keys, int up, int down, int left, int right) {
        double dx = 0, dy = 0;
        if (keys.contains(up))    dy -= 1;
        if (keys.contains(down))  dy += 1;
        if (keys.contains(left))  dx -= 1;
        if (keys.contains(right)) dx += 1;
        if (dx != 0 || dy != 0) juego.movePlayer(index, dx, dy);
    }

    public void refresh() {
        if (juego.getCurrentLevel() != null) {
            GameMode mode = juego.getGameMode();
            boolean twoPlayers = mode == GameMode.PvsP || mode == GameMode.PvsM;
            String label2 = mode == GameMode.PvsM ? "MAQ" : "P2";
            if (twoPlayers) {
                muertes.setText("P1: " + juego.getPlayerDeaths(0) + " muertes  |  " + label2 + ": " + juego.getPlayerDeaths(1) + " muertes");
            } else {
                muertes.setText("MUERTES: " + juego.getPlayerDeaths(0));
            }
            if (twoPlayers) {
                monedas.setText("P1: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0)
                    + "  |  " + label2 + ": " + juego.getPlayerCoins(1) + "/" + juego.getPlayerTotalCoins(1));
            } else {
                monedas.setText("Monedas: " + juego.getPlayerCoins(0) + "/" + juego.getPlayerTotalCoins(0));
            }
            niveles.setText("Nivel: " + juego.getLevelNumber());
            tiempo.setText(twoPlayers ? "" : "Tiempo: " + String.format("%.0f", juego.getRemainingTime()));

            if (juego.isLevelComplete()) {
                boolean hasNext = juego.hasNextLevel();
                juego.advanceLevel();
                if (!hasNext) {
                    gameLoop.stop();
                    keysDownPlayer1.clear();
                    keysDownPlayer2.clear();
                    if (twoPlayers) {
                        mostrarResultadoPvsP();
                    } else {
                        JOptionPane.showMessageDialog(this, "¡HAS GANADO EL JUEGO!");
                    }
                    cardLayout.show(panel, PANEL_INICIO);
                }
            } else if (juego.isGameOver()) {
                gameLoop.stop();
                keysDownPlayer1.clear();
                keysDownPlayer2.clear();
                JOptionPane.showMessageDialog(this, "¡Tiempo agotado!");
                cardLayout.show(panel, PANEL_INICIO);
            }
        }
        tablero.refresh();
    }

    private void mostrarResultadoPvsP() {
        java.util.Map<String, Integer> levelsWon = juego.getLevelsWon();
        int wonP1 = levelsWon.getOrDefault("Player1", 0);
        int wonP2 = levelsWon.getOrDefault("Player2", 0);
        String ganador = wonP1 > wonP2 ? "Player1" : wonP2 > wonP1 ? "Player2" : "Empate";
        String msg = "=== RESULTADO FINAL ===\n"
            + "Player1 — Niveles ganados: " + wonP1
            + "  |  Muertes: " + juego.getPlayerDeaths(0)
            + "  |  Monedas: " + juego.getPlayerLifetimeCoins(0) + "\n"
            + "Player2 — Niveles ganados: " + wonP2
            + "  |  Muertes: " + juego.getPlayerDeaths(1)
            + "  |  Monedas: " + juego.getPlayerLifetimeCoins(1) + "\n\n"
            + (ganador.equals("Empate") ? "¡EMPATE!" : "¡Ganó " + ganador + "!");
        JOptionPane.showMessageDialog(this, msg, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshBorderButtonStates() {
        boolean twoPlayers = selectedMode != GameMode.PLAYER;
        for (int i = 0; i < BORDER_COLORS.length; i++) {
            Color c = BORDER_COLORS[i];
            applyBorderButtonStyle(borde1Botones[i], c,
                c.equals(selectedBorder1),
                twoPlayers && c.equals(selectedBorder2));
            applyBorderButtonStyle(borde2Botones[i], c,
                c.equals(selectedBorder2),
                twoPlayers && c.equals(selectedBorder1));
        }
        if (panelSeleccion != null) {
            panelSeleccion.revalidate();
            panelSeleccion.repaint();
        }
    }

    private void applyBorderButtonStyle(JButton b, Color borderColor, boolean selectedHere, boolean takenByOther) {
        b.setBackground(borderColor);
        b.setForeground(borderColor.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
        b.setOpaque(true);
        b.setBorderPainted(true);
        if (selectedHere) {
            b.setBorder(BorderFactory.createLineBorder(new Color(30, 80, 180), 4));
        } else {
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
        b.setEnabled(!takenByOther);
    }

    private void highlightButton(JButton selected, JButton... others) {
        selected.putClientProperty("flat-selected", Boolean.TRUE);
        selected.repaint();
        for (JButton b : others) {
            b.putClientProperty("flat-selected", Boolean.FALSE);
            b.repaint();
        }
    }

    private static Color contrastText(Color bg) {
        int brightness = (bg.getRed() * 299 + bg.getGreen() * 587 + bg.getBlue() * 114) / 1000;
        return brightness > 150 ? Color.BLACK : Color.WHITE;
    }

    private JButton createFlatButton(String text, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                boolean selected = Boolean.TRUE.equals(getClientProperty("flat-selected"));
                boolean hover = getModel().isRollover() && isEnabled();

                Color bg;
                Color fg;
                int borderW;
                if (selected) {
                    bg = hoverColor;
                    fg = contrastText(hoverColor);
                    borderW = 4;
                } else if (hover) {
                    bg = hoverColor;
                    fg = contrastText(hoverColor);
                    borderW = 2;
                } else {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                    borderW = 2;
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(borderW));
                g2.drawRect(borderW / 2, borderW / 2, w - borderW, h - borderW);

                g2.setFont(getFont());
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setMargin(new Insets(8, 16, 8, 16));
        btn.putClientProperty("flat-selected", Boolean.FALSE);
        return btn;
    }

    private void exit() {
        int option = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la aplicación?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) System.exit(0);
    }
}
