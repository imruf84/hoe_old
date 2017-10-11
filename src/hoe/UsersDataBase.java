package hoe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Felhasználók adatbázisát kezelő osztály.
 *
 * @author imruf84
 */
public class UsersDataBase extends DataBase {

    /**
     * SHA algoritmus iterációinak a száma
     */
    private static final int SHA_LENGTH = 1000;

    /**
     * Konstruktor.
     *
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    public UsersDataBase() throws ClassNotFoundException, SQLException {
        super("users");
    }

    @Override
    protected void createTables() throws SQLException {
        try (Statement stat = getConnection().createStatement()) {
            stat.execute("CREATE TABLE IF NOT EXISTS USERS (ID VARCHAR(20) NOT NULL, password VARCHAR(80) NOT NULL, PRIMARY KEY (ID))");
        }
    }

    /**
     * Felhasználó létezésének ellenörzése.
     *
     * @param user felhasználó
     * @return létezés esetén igaz egyébként hamis
     * @throws SQLException kivétel
     */
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

    /**
     * Felhasználó tárolása.
     *
     * @param user felhasználó
     * @return sikeres tárolás esetén igaz egyébként hamis
     * @throws java.sql.SQLException kivétel
     */
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

    /**
     * Felhasználó hitelesítése.
     *
     * @param user felhasználó
     * @return hiteles felhasználó esetén igaz egyébként hamis
     * @throws SQLException kivétel
     */
    public boolean validateUser(User user) throws SQLException {
        try (
                Statement st = getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM USERS WHERE ID='" + user.getUserName() + "' AND password=HASH('SHA256', STRINGTOUTF8('" + user.getPassword() + "'), " + SHA_LENGTH + ")")) {

            return rs.next();
        }
    }

}
