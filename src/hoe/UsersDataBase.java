package hoe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersDataBase extends DataBase {

    private static final int SHA_LENGTH = 1000;

    public UsersDataBase() throws ClassNotFoundException, SQLException {
        super("users");
    }

    public UsersDataBase(String ip) throws ClassNotFoundException, SQLException {
        super(ip, "users");
    }

    @Override
    protected void createTables() throws SQLException {
        try (Statement stat = getConnection().createStatement()) {
            stat.execute("CREATE TABLE IF NOT EXISTS USERS (ID VARCHAR(20) NOT NULL, password VARCHAR(80) NOT NULL, PRIMARY KEY (ID))");
        }
    }

    public boolean isUserStored(User user) throws SQLException {

        if (null == user) {
            return false;
        }

        try (
                Statement st = getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM USERS WHERE ID='" + user.getUserName() + "'")) {

            return rs.next();
        }

    }

    public synchronized boolean storeUser(User user) throws SQLException {
        if (isUserStored(user)) {
            return false;
        }

        try (
                Statement st = getConnection().createStatement()) {
            st.execute("INSERT INTO USERS (ID, password) VALUES ('" + user.getUserName() + "',HASH('SHA256', STRINGTOUTF8('" + user.getPassword() + "'), " + SHA_LENGTH + "))");
            Log.debug("User stored to database: " + user.getUserName());

            return true;
        }
    }

    public boolean validateUser(User user) throws SQLException {
        try (
                Statement st = getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM USERS WHERE ID='" + user.getUserName() + "' AND password=HASH('SHA256', STRINGTOUTF8('" + user.getPassword() + "'), " + SHA_LENGTH + ")")) {

            return rs.next();
        }
    }

}
