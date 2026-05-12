package domain;
import java.util.logging.*;
import java.util.logging.Level;
import java.io.IOException;

public class GameLogger {
	private static GameLogger instance;
	private static final Logger logger = Logger.getLogger("DOPOHardestGame");
	
	private GameLogger() {
		try {
			//crea un manejador que escribe en el archivo errors.log
			FileHandler handler = new FileHandler("errors.log", true);
			//le dice al manejador como formatear los mensajes
			handler.setFormatter(new SimpleFormatter());
			//conecta el handler al logger
			logger.addHandler(handler);
			logger.setLevel(Level.ALL);
		}catch(IOException e){
			// si falla escribe el error en consola
			logger.severe("No se pudo inicializar el log: " + e.getMessage());
			
		}
	}
	
	//crear una sola instancia de logger
	public static GameLogger getInstance() {
		if (instance == null) instance = new GameLogger();
		return instance;
	}
	
	//para errores graves, recibe el mensaje y la excepcion completa
	public void logError(String message, Exception e) {
	    logger.log(Level.SEVERE, message, e);
	}
	
	//para situaciones raras pero no fatales
	public void logWarning(String message) {
		logger.warning(message);
	}
	
	//para eventos normales	
    public void logInfo(String message) {
        logger.info(message);
    }
}
