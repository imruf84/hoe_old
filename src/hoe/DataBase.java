package hoe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Adatbázis osztály.
 * 
 * @author imruf84
 */
public abstract class DataBase {
    
    public static final String DATA_BASE_PATH = "./data/";
    /**
     * Adatbáziskapcsolat.
     */
    private final Connection connection;
    /**
     * Adatbázis neve.
     */
    private final String dataBaseName;

    /**
     * Konstruktor.
     * 
     * @param dbName adatbázis neve
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    public DataBase(String dbName) throws ClassNotFoundException, SQLException {
        dataBaseName = dbName;
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:" + DATA_BASE_PATH + getDataBaseName() + ";trace_level_file=0;AUTO_SERVER=TRUE", "sa", "");
        
        doThings();
    }
    
    /**
     * Konstruktor utáni dolgok létrehozása.
     * 
     * @throws java.sql.SQLException kivétel
     */
    protected final void doThings() throws SQLException {
        createTables();
    }

    /**
     * Adatbáziskapcsolat lekérdezése.
     * 
     * @return adatbáziskapcsolat
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Adatbázis nevének a lekérdezése.
     * 
     * @return adatbázis neve
     */
    public final String getDataBaseName() {
        return dataBaseName;
    }
    
    /**
     * Adatbázistáblák létrehozása.
     * 
     * @throws java.sql.SQLException kivétel
     */
    protected abstract void createTables() throws SQLException;
    
}
