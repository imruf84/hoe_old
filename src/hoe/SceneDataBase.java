package hoe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SceneDataBase extends DataBase {

    public SceneDataBase() throws ClassNotFoundException, SQLException {
        super("scene");
    }

    public SceneDataBase(String ip) throws ClassNotFoundException, SQLException {
        super(ip, "scene");
    }

    @Override
    protected void createTables() throws SQLException {
        try (Statement stat = getConnection().createStatement()) {
            // Messages.
            stat.execute("CREATE TABLE IF NOT EXISTS MESSAGES (USER VARCHAR(20) NOT NULL, MSG TEXT NOT NULL, TIME TIMESTAMP NOT NULL)");
            // Objects.
            stat.execute("CREATE TABLE IF NOT EXISTS OBJECTS (ID IDENTITY NOT NULL, MASS BIGINT NOT NULL, DIAMETER BIGINT NOT NULL, POINTS BIGINT NOT NULL, OWNER VARCHAR(20) NOT NULL, TYPE VARCHAR(10) NOT NULL, PRIMARY KEY (ID));");
            // Positions.
            stat.execute("CREATE TABLE IF NOT EXISTS POSITIONS (STEP BIGINT NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, VX DOUBLE NOT NULL, VY DOUBLE NOT NULL, OBJECT_ID BIGINT NOT NULL, FOREIGN KEY (OBJECT_ID) REFERENCES OBJECTS (ID) ON DELETE CASCADE);");
        }
    }

    public synchronized boolean storeMessage(User user, String msg) throws SQLException {

        Log.debug("storeMessage[" + user.getUserName() + "]: msg=" + msg);

        try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO MESSAGES (USER,MSG,TIME) VALUES (?,?,NOW())")) {
            ps.setString(1, user.getUserName());
            ps.setString(2, msg);
            ps.execute();
        }

        return true;
    }

    public String getMessagesAsJson(int count) throws SQLException {

        String result = JsonUtil.chatMessage("", "") + ",";
        boolean firts = true;

        try (
                PreparedStatement ps = getConnection().prepareCall("SELECT * FROM (SELECT * FROM MESSAGES ORDER BY TIME DESC LIMIT " + count + ") ORDER BY TIME");
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                if (firts) {
                    result = "";
                    firts = false;
                }
                result += JsonUtil.chatMessage(rs.getString("USER"), rs.getString("MSG")) + ",";
            }

        }

        return result.substring(0, result.length() - 1);
    }

    public void storeMeteor(Meteor m) throws SQLException {

        // Storing meteor.
        try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO OBJECTS (MASS, DIAMETER, POINTS, OWNER, TYPE) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, m.getMass());
            ps.setLong(2, m.getDiameter());
            ps.setLong(3, m.getPoints());
            ps.setString(4, m.getOwner());
            ps.setString(5, Meteor.OBJECT_TYPE);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()) {
                m.setID(rs.getLong(1));
            }
        }

        // Storing position.
        try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO POSITIONS (STEP, X, Y, VX, VY, OBJECT_ID) VALUES (?,?,?,?,?,?)")) {

            ps.setLong(1, 0);
            ps.setDouble(2, m.getPosition().x);
            ps.setDouble(3, m.getPosition().y);
            ps.setDouble(4, m.getVelocity().x);
            ps.setDouble(5, m.getVelocity().y);
            ps.setLong(6, m.getID());

            ps.execute();
        }
    }

    public void removeAllObjects() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM OBJECTS; ALTER TABLE OBJECTS ALTER COLUMN ID RESTART WITH 1;")) {
            ps.execute();
        }
    }

}
