package hoe;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Játéktér.
 * 
 * @author imruf84
 */
public class Universe {
    
    /**
     * Játéktér.
     */
    private static UniverseDataBase universe;
    
    /**
     * Inicializálás.
     * 
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    public static void init() throws ClassNotFoundException, SQLException {
        Log.info("Init universe...");
        connectToDataBase();
    }
    
    /**
     * Adatbáziskapcsolat létrehozása.
     *
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    private static void connectToDataBase() throws ClassNotFoundException, SQLException {
        universe = new UniverseDataBase();
    }

    /**
     * Üzenetkezelő lekérdezése.
     * 
     * @return üzenetkezelő
     */
    public static UniverseDataBase getUniverse() {
        return universe;
    }
    
    /**
     * Meteor tárolása.
     * 
     * @param m meteor
     * @throws java.sql.SQLException kivétel
     */
    public static void addMeteor(Meteor m) throws SQLException {
        getUniverse().storeMeteor(m);
    }
    
    /**
     * Univerzum generálása.
     * 
     * @throws java.sql.SQLException kivétel
     * @throws java.io.IOException kivétel
     */
    public static void generate() throws SQLException, IOException {
        
        Log.info("Generating universe...");
        Game.setState(Game.GENERATE);
        getUniverse().removeAllObjects();
        
        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
