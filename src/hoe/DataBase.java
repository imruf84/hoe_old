package hoe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DataBase {

    public static final String IP_PATTERN = "[IP]";
    public static final String DATA_BASE_PATH = "tcp://" + IP_PATTERN + "/./data/";
    public String ip;
    public static final String DATA_BASE_USER = "sa";
    public static final String DATA_BASE_PASSWORD = "12345";
    private Connection connection;
    private final String dataBaseName;

    public DataBase(String ip, String dbName) throws ClassNotFoundException, SQLException {
        dataBaseName = dbName;
        setIp(ip == null ? "localhost" : ip);
        Class.forName("org.h2.Driver");
        createConnection();

        doThings();
        
        //compact();
    }
    
    private void createConnection() throws SQLException {
        connection = DriverManager.getConnection(""
                + "jdbc:h2:"
                + DATA_BASE_PATH.replace(IP_PATTERN, getIp())
                + getDataBaseName() + ";trace_level_file=0;DB_CLOSE_DELAY=-1;MV_STORE=FALSE;",
                DATA_BASE_USER, DATA_BASE_PASSWORD);
    }
    
    public void reconnect() throws SQLException {
        if (getConnection().isClosed()) {
            createConnection();
        }
    }

    public final void setIp(String ip) {
        this.ip = ip;
    }

    public final String getIp() {
        return ip;
    }
    
    public DataBase(String dbName) throws ClassNotFoundException, SQLException {
        this("localhost", dbName);
    }

    protected final void doThings() throws SQLException {
        createTables();
    }

    public Connection getConnection() {
        return connection;
    }

    public final String getDataBaseName() {
        return dataBaseName;
    }
    
    public final void compact() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("SHUTDOWN COMPACT;")) {
            ps.execute();
            reconnect();
        }
    }

    protected abstract void createTables() throws SQLException;

}
