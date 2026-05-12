package domain;

public class GameDataAccess {
	private static GameDataAccess instance;
	
	
    public static GameDataAccess getInstance() {
        if (instance == null) {
        	instance = new GameDataAccess();
        	GameLogger.getInstance().logInfo("GameDataAccess inicializado");
        }
        
        return instance;
    }
    
    
    public void loadLevel(String file) {
    	
    }

}
