package hoe;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Játék alaposztálya.
 * 
 * @author imruf84
 */
public class Game {
    
    /**
     * Inicializálás.
     */
    public static final String INIT = "init";
    /**
     * Univerzum generálása.
     */
    public static final String GENERATE = "generate";
    /**
     * Várakozó állás.
     */
    public static final String WAIT = "wait";
    /**
     * Szimuláció futtatása.
     */
    public static final String SIMULATE = "simulate";
    
    /**
     * Aktuális játékállapot.
     */
    public static String currentState = "";
    
    /**
     * Inicializálása.
     * 
     * @throws java.sql.SQLException kivétel
     * @throws java.io.IOException kivétel
     */
    public static void init() throws SQLException, IOException {
        Log.info("Init game...");
        setState(INIT);
        start();
    }
    
    /**
     * Játék indítása.
     * 
     * @throws java.sql.SQLException kivétel
     * @throws java.io.IOException kivétel
     */
    public static void start() throws SQLException, IOException {
        Log.info("Starting game...");
        
        Universe.generate();
        
        // Kezdődhet a játék.
        setState(WAIT);
    }
    
    /**
     * Játékállapot megadása.
     * 
     * @param state állapot
     * @throws java.io.IOException kivétel
     */
    public static void setState(String state) throws IOException {
        
        if (state.toUpperCase().equals(getCurrentState().toUpperCase())) return;
        
        currentState = state;
        Log.debug("Game state is changed to: " + state);
        
        // Felhasználók tájékoztatása.
        UserManager.sendMessageToAll(getStateChangedMessage());
    }
    
    /**
     * Játékállapot vlátozásának szöveges üzenetének lekérdezése.
     * 
     * @return játékállapot változásának az üzenete
     */
    public static String getStateChangedMessage() {
        return "{\"a\":\"gsc\",\"d\":{\"state\":\"" + getCurrentState() + "\"}}";
    }

    /**
     * Aktuális állapot lekérdezése.
     * 
     * @return aktuális állapot
     */
    public static String getCurrentState() {
        return currentState;
    }
}
