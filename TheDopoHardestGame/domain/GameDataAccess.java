package domain;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataAccess {
	private static final String LEVELS_PATH = "resources/levels/";
	/** Conversion factor: how many ticks the level file's "TIME=N seconds" maps to. Must match the GameLoop tick rate. */
	public static final int TICKS_PER_SECOND = 60;
	private static GameDataAccess instance;
	
	
	private GameDataAccess() {
		
	}	
	
	
    public static GameDataAccess getInstance() {
        if (instance == null) {
        	instance = new GameDataAccess();
        	GameLogger.getInstance().logInfo("GameDataAccess inicializado");
        }
        
        return instance;
    }
    
    //convierte un array de strings en un mapa de pares clave-valor
    
    private Map<String, String> parseParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        for (String pair : paramsStr.split(",")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return params;
    }
    
    public Level loadLevel(String file) {
        return loadLevelAbsolute(new File(LEVELS_PATH + file));
    }

    public Level loadLevelAbsolute(File file) {
    	Integer number = null;
        Double time = null;
        GameMap map = new GameMap(800, 500);
        List<String> elementLines = new ArrayList<>(); //todo lo que no sea configuración

    	try (
    		BufferedReader reader = new BufferedReader(
    				new FileReader(file))){
    		
    		String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("NUMBER=")) {
                    number = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("TIME=")) {
                    time = Double.parseDouble(line.split("=")[1]);
                } else {
                    elementLines.add(line);
                }
            }
        } catch (IOException e) {
            GameLogger.getInstance().logError("Error leyendo nivel: " + file.getName(), e);
            return null;
        }
    	
        if (number == null || time == null) {
            try {
                throw new GameException("Faltan NUMBER o TIME en: " + file);
            } catch (GameException e) {
                GameLogger.getInstance().logError("Nivel inválido", e);
                return null;
            }
        }
        
        // Convert seconds from the file into ticks (the domain's time unit).
        int timeInTicks = (int) Math.round(time * TICKS_PER_SECOND);
        Level level = new Level(number, timeInTicks, map);
        
        for (String elementLine: elementLines) {
        	int spaceIdx = elementLine.indexOf(' ');
        	String type = spaceIdx == -1 ? elementLine : elementLine.substring(0, spaceIdx);
        	Map<String, String> p = spaceIdx == -1
        	    ? new HashMap<>()
        	    : parseParams(elementLine.substring(spaceIdx + 1));
        	
        	
        	switch(type) {
	        	case "PLAYER":
	        		Player player;
	                switch (p.get("type")) {
	                case "blue":
	                    player = new BluePlayer("Player",
	                        Double.parseDouble(p.get("x")),
	                        Double.parseDouble(p.get("y")));
	                    break;
	                case "green":
	                    player = new GreenPlayer("Player",
	                        Double.parseDouble(p.get("x")),
	                        Double.parseDouble(p.get("y")));
	                    break;
	                default:
	                    player = new RedPlayer("Player",
	                        Double.parseDouble(p.get("x")),
	                        Double.parseDouble(p.get("y")));
	                    break;
	                }
	                level.addPlayer(player);
	                break;
	            
	            case "WALL":
	                level.addStaticElement(new SolidWall(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height")),
	                    "black"
	                ));
	                break;
	                
	            case "COIN":
	                if ("blue".equals(p.get("type")) || "green".equals(p.get("type")) || "red".equals(p.get("type"))) {
	                    level.addCoin(new SkinCoin(
	                        Double.parseDouble(p.get("x")),
	                        Double.parseDouble(p.get("y")),
	                        Double.parseDouble(p.get("width")),
	                        Double.parseDouble(p.get("height")),
	                        p.get("type")
	                    ));
	                } else {
	                    level.addCoin(new Coin(
	                        Double.parseDouble(p.get("x")),
	                        Double.parseDouble(p.get("y")),
	                        Double.parseDouble(p.get("width")),
	                        Double.parseDouble(p.get("height")),
	                        p.get("type")
	                    ));
	                }
	                break;
	                
	            case "ENEMY":
	                MovementStrategy movement;
	                String movementType = p.get("movement");
	                if ("patrol".equals(movementType)) {
	                    String[] points = p.get("route").split("\\|");
	                    Point2D.Double[] route = new Point2D.Double[points.length];
	                    for (int i = 0; i < points.length; i++) {
	                        String[] xy = points[i].split(":");
	                        route[i] = new Point2D.Double(
	                            Double.parseDouble(xy[0]),
	                            Double.parseDouble(xy[1])
	                        );
	                    }
	                    movement = PatrolMovement.basic(route);
	                } else {
	                    LinearMovement.Direction dir =
	                        LinearMovement.Direction.valueOf(p.get("direction"));
	                    int sign = Integer.parseInt(p.get("sign"));
	                    movement = "accelerated".equals(movementType)
	                        ? LinearMovement.accelerated(dir, sign)
	                        : LinearMovement.basic(dir, sign);
	                }
	                level.addEnemy(new Enemy(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height")),
	                    movement
	                ));
	                break;
	                
	            case "LIFESOURCE":
	                level.addStaticElement(new LifeSource(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height")),
	                    "yellow"
	                ));
	                break;

	            case "BOMB":
	                level.addStaticElement(new Bomb(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height"))
	                ));
	                break;
	                
	            case "INITIAL_ZONE":
	                level.addZone("initial", new InitialZone(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height"))
	                ));
	                break;

	            case "FINAL_ZONE":
	                level.addZone("final", new FinalZone(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height"))
	                ));
	                break;

	            case "INTERMEDIATE_ZONE":
	                level.addZone("intermediate", new IntermediateZone(
	                    Double.parseDouble(p.get("x")),
	                    Double.parseDouble(p.get("y")),
	                    Double.parseDouble(p.get("width")),
	                    Double.parseDouble(p.get("height"))
	                ));
	                break;
	                
	            default:
	                GameLogger.getInstance().logWarning(
	                    "Tipo desconocido en nivel: " + type);
	                break;
	        	}    		
    		}
        	
	        GameLogger.getInstance().logInfo("Nivel " + number + " cargado desde " + file.getName());
	        return level;

    	}

    public void guardarPartida(TheDOPOHardestGame game, File file) throws GameException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(game);
            GameLogger.getInstance().logInfo("Partida guardada en " + file.getAbsolutePath());
        } catch (IOException e) {
            GameLogger.getInstance().logError("Error al guardar la partida", e);
            throw new GameException("Error al guardar la partida: " + e.getMessage());
        }
    }

    public TheDOPOHardestGame abrirPartida(File file) throws GameException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            TheDOPOHardestGame game = (TheDOPOHardestGame) in.readObject();
            GameLogger.getInstance().logInfo("Partida abierta desde " + file.getAbsolutePath());
            return game;
        } catch (IOException | ClassNotFoundException e) {
            GameLogger.getInstance().logError("Error al abrir la partida", e);
            throw new GameException("Error al abrir la partida: " + e.getMessage());
        }
    }

    public void exportarNivel(Level level, File file) throws GameException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("NUMBER=" + level.getNumber());
            // Export back to seconds (the file format's unit).
            writer.println("TIME=" + (level.getGameTime() / (double) TICKS_PER_SECOND));
            for (Player p : level.getPlayers()) {
                writer.println("PLAYER x=" + p.getSpawnX() + ",y=" + p.getSpawnY()
                        + ",type=" + p.getTypeName());
            }
            for (StaticElement e : level.getStaticElements()) {
                writer.println(e.getFileType() + " x=" + e.getX() + ",y=" + e.getY()
                        + ",width=" + e.getWidth() + ",height=" + e.getHeight());
            }
            for (Enemy e : level.getEnemies()) {
                writer.println("ENEMY x=" + e.getX() + ",y=" + e.getY()
                        + ",width=" + e.getWidth() + ",height=" + e.getHeight()
                        + "," + e.getMovement().toFileParams());
            }
            for (Coin c : level.getCoins()) {
                writer.println("COIN x=" + c.getX() + ",y=" + c.getY()
                        + ",width=" + c.getWidth() + ",height=" + c.getHeight()
                        + ",type=" + c.getCoinType());
            }
            for (Map.Entry<String, Zone> entry : level.getZones().entrySet()) {
                Zone z = entry.getValue();
                writer.println(entry.getKey().toUpperCase() + "_ZONE x=" + z.getX() + ",y=" + z.getY()
                        + ",width=" + z.getWidth() + ",height=" + z.getHeight());
            }
            GameLogger.getInstance().logInfo("Nivel exportado en " + file.getAbsolutePath());
        } catch (IOException e) {
            GameLogger.getInstance().logError("Error al exportar nivel", e);
            throw new GameException("Error al exportar nivel: " + e.getMessage());
        }
    }
}
