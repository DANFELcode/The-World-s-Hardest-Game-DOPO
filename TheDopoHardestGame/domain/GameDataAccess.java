package domain;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private GameDataAccess() { }

    public static GameDataAccess getInstance() {
        if (instance == null) {
            instance = new GameDataAccess();
        }
        return instance;
    }

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

    /** Returns how many level files exist for the given mode. */
    public int getLevelCount(GameMode mode) {
        String subfolder = mode == GameMode.PvsP ? "pvsp/" : "player/";
        int count = 0;
        while (new File(LEVELS_PATH + subfolder + "level" + (count + 1) + ".txt").exists()) {
            count++;
        }
        return count;
    }

    /** Loads a level for the given mode. Returns null on error (already logged via exception auto-log). */
    public Level loadLevel(String file, GameMode mode) {
    	String subfolder = (mode == GameMode.PLAYER) ? "player/" : "pvsp/";
        try {
            return loadLevelAbsolute(new File(LEVELS_PATH + subfolder + file));
        } catch (GameException e) {
            return null;
        }
    }

    public Level loadLevelAbsolute(File file) throws LevelFormatException, LevelIOException {
        String fileName = file.getName();
        Integer number = null;
        Double time = null;
        GameMap map = new GameMap(800, 500);
        List<String> elementLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("NUMBER=")) {
                    number = parseIntField(fileName, "NUMBER", line.split("=")[1]);
                } else if (line.startsWith("TIME=")) {
                    time = parseDoubleField(fileName, "TIME", line.split("=")[1]);
                } else {
                    elementLines.add(line);
                }
            }
        } catch (IOException e) {
            throw new LevelIOException(fileName, "Error leyendo nivel: " + e.getMessage());
        }

        if (number == null) throw new LevelFormatException(fileName, "Falta campo NUMBER");
        if (time == null)   throw new LevelFormatException(fileName, "Falta campo TIME");
        if (time <= 0)      throw new LevelFormatException(fileName, "TIME debe ser > 0");

        // Convert seconds from the file into ticks (the domain's time unit).
        int timeInTicks = (int) Math.round(time * TICKS_PER_SECOND);
        Level level = new Level(number, timeInTicks, map);

        for (String elementLine : elementLines) {
            int spaceIdx = elementLine.indexOf(' ');
            String type = spaceIdx == -1 ? elementLine : elementLine.substring(0, spaceIdx);
            Map<String, String> p = spaceIdx == -1
                ? new HashMap<>()
                : parseParams(elementLine.substring(spaceIdx + 1));

            switch (type) {
                case "WALL":
                case "LIFESOURCE":
                case "BOMB":              level.addStaticElement(createStaticElement(fileName, map, type, p)); break;
                case "COIN":              level.addCoin(createCoin(fileName, map, p)); break;
                case "ENEMY":             level.addEnemy(createEnemy(fileName, map, p)); break;
                case "INITIAL_ZONE":
                case "FINAL_ZONE":
                case "INTERMEDIATE_ZONE": addZone(fileName, map, level, type, p); break;
                default: throw new LevelFormatException(fileName, "Tipo desconocido: " + type);
            }
        }

        validateInitialZones(fileName, level);
        return level;
    }

    /** Ensures every InitialZone is large enough for a player and the player's spawn area is wall-free. */
    private void validateInitialZones(String filePath, Level level) throws LevelFormatException {
        for (Map.Entry<String, Zone> entry : level.getZones().entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith("initial_")) continue;
            Zone zone = entry.getValue();
            if (zone.getWidth() < MIN_PLAYER_SIZE || zone.getHeight() < MIN_PLAYER_SIZE) {
                throw new LevelFormatException(filePath,
                    key + " es más pequeña que el tamaño mínimo de jugador ("
                    + MIN_PLAYER_SIZE + "x" + MIN_PLAYER_SIZE + ")");
            }
            // El jugador respawnea en la esquina superior izquierda de la zona.
            java.awt.geom.Rectangle2D spawn = new java.awt.geom.Rectangle2D.Double(
                zone.getX(), zone.getY(), MIN_PLAYER_SIZE, MIN_PLAYER_SIZE);
            for (StaticElement e : level.getStaticElements()) {
                if (e.isBlocking() && e.getAreaColision().intersects(spawn)) {
                    throw new LevelFormatException(filePath,
                        key + ": el spawn (" + zone.getX() + "," + zone.getY()
                        + ") se solapa con un elemento bloqueante en ("
                        + e.getX() + "," + e.getY() + ")");
                }
            }
        }
    }

    private static final double MIN_PLAYER_SIZE = GameConstants.MIN_PLAYER_SIZE;

    // ===== Validation helpers =====

    private int parseIntField(String filePath, String name, String value) throws LevelFormatException {
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) {
            throw new LevelFormatException(filePath, "Campo '" + name + "' no es entero: " + value);
        }
    }

    private double parseDoubleField(String filePath, String name, String value) throws LevelFormatException {
        try { return Double.parseDouble(value.trim()); }
        catch (NumberFormatException e) {
            throw new LevelFormatException(filePath, "Campo '" + name + "' no es numérico: " + value);
        }
    }

    private double requireDouble(String filePath, Map<String, String> p, String key) throws LevelFormatException {
        String v = p.get(key);
        if (v == null) throw new LevelFormatException(filePath, "Falta campo '" + key + "'");
        return parseDoubleField(filePath, key, v);
    }

    private String requireString(String filePath, Map<String, String> p, String key) throws LevelFormatException {
        String v = p.get(key);
        if (v == null) throw new LevelFormatException(filePath, "Falta campo '" + key + "'");
        return v;
    }

    private void requirePositive(String filePath, String name, double value) throws LevelFormatException {
        if (value <= 0) throw new LevelFormatException(filePath, name + " debe ser > 0 (valor=" + value + ")");
    }

    private void requireInBounds(String filePath, String elementType,
                                 double x, double y, double w, double h, GameMap map) throws LevelFormatException {
        if (x < 0 || y < 0 || x + w > map.getWidth() || y + h > map.getHeight()) {
            throw new LevelFormatException(filePath,
                elementType + " fuera de límites del mapa (" + map.getWidth() + "x" + map.getHeight()
                + "): x=" + x + ",y=" + y + ",w=" + w + ",h=" + h);
        }
    }

    // ===== Element factories =====

    private StaticElement createStaticElement(String filePath, GameMap map, String type, Map<String, String> p) throws LevelFormatException {
        double x = requireDouble(filePath, p, "x");
        double y = requireDouble(filePath, p, "y");
        double w = requireDouble(filePath, p, "width");
        double h = requireDouble(filePath, p, "height");
        requirePositive(filePath, "width", w);
        requirePositive(filePath, "height", h);
        requireInBounds(filePath, type, x, y, w, h, map);
        switch (type) {
            case "WALL":       return new SolidWall(x, y, w, h, "black");
            case "LIFESOURCE": return new LifeSource(x, y, w, h, "pink", p.getOrDefault("owner", "Player1"));
            case "BOMB":       return new Bomb(x, y, w, h);
            default: throw new LevelFormatException(filePath, "Static element desconocido: " + type);
        }
    }

    private Coin createCoin(String filePath, GameMap map, Map<String, String> p) throws LevelFormatException {
        double x = requireDouble(filePath, p, "x");
        double y = requireDouble(filePath, p, "y");
        double w = requireDouble(filePath, p, "width");
        double h = requireDouble(filePath, p, "height");
        requirePositive(filePath, "width", w);
        requirePositive(filePath, "height", h);
        requireInBounds(filePath, "COIN", x, y, w, h, map);
        String skinType = p.get("type");
        String owner = p.getOrDefault("owner", "Player1");
        if ("blue".equals(skinType) || "green".equals(skinType) || "red".equals(skinType)) {
            return new SkinCoin(x, y, w, h, skinType, owner);
        }
        return new Coin(x, y, w, h, skinType, owner);
    }

    private Enemy createEnemy(String filePath, GameMap map, Map<String, String> p) throws LevelFormatException {
        double x = requireDouble(filePath, p, "x");
        double y = requireDouble(filePath, p, "y");
        double w = requireDouble(filePath, p, "width");
        double h = requireDouble(filePath, p, "height");
        requirePositive(filePath, "width", w);
        requirePositive(filePath, "height", h);
        requireInBounds(filePath, "ENEMY", x, y, w, h, map);
        String movementType = requireString(filePath, p, "movement");
        MovementStrategy movement;

        if ("patrol".equals(movementType)) {
            String route = requireString(filePath, p, "route");
            String[] points = route.split("\\|");
            Point2D.Double[] routePts = new Point2D.Double[points.length];
            for (int i = 0; i < points.length; i++) {
                String[] xy = points[i].split(":");
                if (xy.length != 2) throw new LevelFormatException(filePath, "Punto de ruta inválido: " + points[i]);
                routePts[i] = new Point2D.Double(
                    parseDoubleField(filePath, "route.x", xy[0]),
                    parseDoubleField(filePath, "route.y", xy[1]));
            }
            movement = PatrolMovement.basic(routePts);
        } else if ("basic".equals(movementType) || "accelerated".equals(movementType)) {
            String dirStr = requireString(filePath, p, "direction");
            Direction dir;
            try { dir = Direction.valueOf(dirStr); }
            catch (IllegalArgumentException e) {
                throw new LevelFormatException(filePath, "Dirección inválida: " + dirStr);
            }
            int sign = parseIntField(filePath, "sign", requireString(filePath, p, "sign"));
            if (sign != 1 && sign != -1) {
                throw new LevelFormatException(filePath, "sign debe ser 1 o -1 (valor=" + sign + ")");
            }
            movement = "accelerated".equals(movementType)
                ? LinearMovement.accelerated(dir, sign)
                : LinearMovement.basic(dir, sign);
        } else {
            throw new LevelFormatException(filePath, "movement desconocido: " + movementType);
        }
        return new Enemy(x, y, w, h, movement);
    }

    private void addZone(String filePath, GameMap map, Level level, String type, Map<String, String> p) throws LevelFormatException {
        double x = requireDouble(filePath, p, "x");
        double y = requireDouble(filePath, p, "y");
        double w = requireDouble(filePath, p, "width");
        double h = requireDouble(filePath, p, "height");
        requirePositive(filePath, "width", w);
        requirePositive(filePath, "height", h);
        requireInBounds(filePath, type, x, y, w, h, map);
        switch (type) {
            case "INITIAL_ZONE": {
                String owner = p.getOrDefault("owner", "Player1");
                level.addZone("initial_" + owner, new InitialZone(x, y, w, h, owner));
                break;
            }
            case "FINAL_ZONE": {
                String owner = p.getOrDefault("owner", "Player1");
                level.addZone("final_" + owner, new FinalZone(x, y, w, h, owner));
                break;
            }
            case "INTERMEDIATE_ZONE":
                level.addZone("intermediate", new IntermediateZone(x, y, w, h));
                break;
        }
    }

    public void guardarPartida(TheDOPOHardestGame game, File file) throws GameException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("mode=" + game.getGameMode().name());
            writer.println("level=" + game.getCurrentLevelNumber());
            for (Player p : game.getPlayers()) {
                String owner = p.getName();
                writer.println("player." + owner + ".type=" + p.getTypeName());
                writer.println("player." + owner + ".deaths=" + p.getDeaths());
                writer.println("player." + owner + ".lifetime=" + p.getCoinsCollected());
                writer.println("player." + owner + ".borderColor=" + p.getBorderColor().getRGB());
            }
            for (Map.Entry<String, Integer> entry : game.getLevelsWon().entrySet()) {
                writer.println("levelsWon." + entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            throw new PersistenceException("save", "Error al guardar la partida: " + e.getMessage());
        }
    }

    public Map<String, String> abrirPartida(File file) throws GameException {
        Map<String, String> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int idx = line.indexOf('=');
                if (idx > 0) data.put(line.substring(0, idx), line.substring(idx + 1));
            }
        } catch (IOException e) {
            throw new PersistenceException("open", "Error al abrir la partida: " + e.getMessage());
        }
        return data;
    }

    public void exportarNivel(Level level, File file) throws GameException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("NUMBER=" + level.getNumber());
            writer.println("TIME=" + (level.getGameTime() / (double) TICKS_PER_SECOND));
            for (StaticElement e : level.getStaticElements()) {
                writer.println(e.getFileType() + " x=" + e.getX() + ",y=" + e.getY()
                        + ",width=" + e.getWidth() + ",height=" + e.getHeight()
                        + e.extraFileParams());
            }
            for (Enemy e : level.getEnemies()) {
                writer.println("ENEMY x=" + e.getX() + ",y=" + e.getY()
                        + ",width=" + e.getWidth() + ",height=" + e.getHeight()
                        + "," + e.getMovement().toFileParams());
            }
            for (Coin c : level.getCoins()) {
                writer.println("COIN x=" + c.getX() + ",y=" + c.getY()
                        + ",width=" + c.getWidth() + ",height=" + c.getHeight()
                        + ",type=" + c.getCoinType()
                        + ",owner=" + c.getOwnerName());
            }
            for (Map.Entry<String, Zone> entry : level.getZones().entrySet()) {
                String key = entry.getKey();
                Zone z = entry.getValue();
                String line;
                if (key.startsWith("initial_")) {
                    line = "INITIAL_ZONE x=" + z.getX() + ",y=" + z.getY()
                            + ",width=" + z.getWidth() + ",height=" + z.getHeight()
                            + ",owner=" + key.substring("initial_".length());
                } else if (key.startsWith("final_")) {
                    line = "FINAL_ZONE x=" + z.getX() + ",y=" + z.getY()
                            + ",width=" + z.getWidth() + ",height=" + z.getHeight()
                            + ",owner=" + key.substring("final_".length());
                } else {
                    line = key.toUpperCase() + "_ZONE x=" + z.getX() + ",y=" + z.getY()
                            + ",width=" + z.getWidth() + ",height=" + z.getHeight();
                }
                writer.println(line);
            }
        } catch (IOException e) {
            throw new LevelIOException(file.getName(), "Error al exportar nivel: " + e.getMessage());
        }
    }
}
