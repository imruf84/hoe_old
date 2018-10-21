package hoe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DataBase {

    public static final String IP_PATTERN = "[IP]";
    public static final String DATA_BASE_PATH = "tcp://" + IP_PATTERN + "/./data/";
    public String ip;
    public static final String DATA_BASE_USER = "sa";
    public static final String DATA_BASE_PASSWORD = "12345";
    private final Connection connection;
    private final String dataBaseName;

    public DataBase(String ip, String dbName) throws ClassNotFoundException, SQLException {
        dataBaseName = dbName;
        this.ip = (ip == null ? "localhost" : ip);
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:" + DATA_BASE_PATH.replace(IP_PATTERN, this.ip) + getDataBaseName() + ";trace_level_file=0;", "sa", "12345");

        doThings();
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

    protected abstract void createTables() throws SQLException;

}
