package hoe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import hoe.servlets.TileRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SceneDataBase extends DataBase {

    public static final String SCENE_PROPERTY_LENGTH = "length";
    public static final String SCENE_PROPERTY_WIDTH = "width";
    public static final String SCENE_PROPERTY_HEIGHT = "height";
    public static final String SCENE_PROPERTY_TILE_BOUNDS = "tile_bounds";
    public static final String SCENE_PROPERTY_CURRENT_TURN_FRAME = "current_turn_frame";
    public static final String SCENE_PROPERTY_KEY_TURN = "turn";
    public static final String SCENE_PROPERTY_KEY_FRAME = "frame";

    public SceneDataBase() throws ClassNotFoundException, SQLException {
        super("scene");
    }

    public SceneDataBase(String ip) throws ClassNotFoundException, SQLException {
        super(ip, "scene");
    }

    @Override
    protected void createTables() throws SQLException {
        try (Statement stat = getConnection().createStatement()) {

            // Scene properties.
            stat.execute("CREATE TABLE IF NOT EXISTS SCENE_POPERTIES (NAME VARCHAR(20) NOT NULL, VALUE VARCHAR(200) NOT NULL)");

            // Messages.
            stat.execute("CREATE TABLE IF NOT EXISTS MESSAGES (USER VARCHAR(20) NOT NULL, MSG TEXT NOT NULL, TIME TIMESTAMP NOT NULL)");
            // Objects.
            stat.execute("CREATE TABLE IF NOT EXISTS OBJECTS (ID IDENTITY NOT NULL, MASS BIGINT NOT NULL, DIAMETER BIGINT NOT NULL, POINTS BIGINT NOT NULL, OWNER VARCHAR(20) NOT NULL, TYPE VARCHAR(10) NOT NULL, PRIMARY KEY (ID));");
            // Positions.
            stat.execute("CREATE TABLE IF NOT EXISTS POSITIONS (STEP BIGINT NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, VX DOUBLE NOT NULL, VY DOUBLE NOT NULL, OBJECT_ID BIGINT NOT NULL, FOREIGN KEY (OBJECT_ID) REFERENCES OBJECTS (ID) ON DELETE CASCADE);");
            // Tiles.
            stat.execute("CREATE TABLE IF NOT EXISTS TILES (TURN BIGINT NOT NULL, FRAME BIGINT NOT NULL, X INT NOT NULL, Y INT NOT NULL, RENDER_TIME BIGINT DEFAULT NULL,TILE BINARY(170000) DEFAULT NULL);");
            stat.execute("CREATE UNIQUE INDEX IF NOT EXISTS PK_TILES ON TILES (TURN,FRAME,X,Y);");
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

    public synchronized int updateTile(long turn, long frame, int x, int y, byte[] tile, long renderTime) throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("UPDATE TILES SET TILE=?,RENDER_TIME=? WHERE X=? AND Y=? AND TURN=? AND FRAME=?;")) {

            ps.setBytes(1, tile);
            ps.setLong(2, renderTime);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setLong(5, turn);
            ps.setLong(6, frame);

            return ps.executeUpdate();
        }
    }

    public synchronized void storeTile(long turn, long frame, int x, int y, byte[] tile) throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO TILES (TURN, FRAME, X, Y, TILE) VALUES (?,?,?,?,?)")) {

            ps.setLong(1, turn);
            ps.setLong(2, frame);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setBytes(5, tile);

            ps.execute();
        }
    }

    public synchronized byte[] getTile(long turn, long frame, int x, int y) throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT TILE FROM TILES WHERE TURN=? AND FRAME=? AND X=? AND Y=?")) {

            ps.setLong(1, turn);
            ps.setLong(2, frame);
            ps.setInt(3, x);
            ps.setInt(4, y);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes(1);
                }
            }

        }

        return null;
    }

    public long getRenderTimeAvg(long turn) throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT AVG(RENDER_TIME) AS T FROM TILES WHERE TURN=? AND NOT RENDER_TIME IS NULL; ")) {

            ps.setLong(1, turn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

        }

        return 0;
    }

    public synchronized void storeMeteor(Meteor m) throws SQLException {

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

    public synchronized boolean getMeteor(Meteor m) throws SQLException {

        boolean b = false;

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM OBJECTS WHERE ID=?", Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, m.getID());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b = true;
                    m.setDiameter(rs.getLong("DIAMETER"));
                    m.setMass(rs.getLong("MASS"));
                    m.setOwner(rs.getString("OWNER"));
                    m.setPoints(rs.getLong("POINTS"));
                }
            }
        }

        // Getting position.
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM POSITIONS WHERE OBJECT_ID=?")) {

            ps.setLong(1, m.getID());

            try (ResultSet rs = ps.executeQuery()) {
                if (b && rs.next()) {
                    m.setPosition(rs.getDouble("X"), rs.getDouble("Y"));
                    m.setVelocity(rs.getDouble("VX"), rs.getDouble("VY"));
                }
            }
        }

        return b;
    }

    public synchronized void removeAllObjects() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM OBJECTS; ALTER TABLE OBJECTS ALTER COLUMN ID RESTART WITH 1;")) {
            ps.execute();
        }
    }

    public synchronized void removeAllTiles() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM TILES;")) {
            ps.execute();
        }
    }

    public synchronized void setSceneProperty(String name, String value) throws SQLException {

        if (getSceneProperty(name) == null) {
            try (PreparedStatement ps = getConnection().prepareStatement("INSERT INTO SCENE_POPERTIES (NAME,VALUE) VALUES (?,?);")) {
                ps.setString(1, name);
                ps.setString(2, value);
                ps.execute();
            }
        } else {
            try (PreparedStatement ps = getConnection().prepareStatement("UPDATE SCENE_POPERTIES SET VALUE=? WHERE NAME=?;")) {
                ps.setString(1, value);
                ps.setString(2, name);
                ps.executeUpdate();
            }
        }
    }

    public synchronized String getSceneProperty(String name) throws SQLException {

        String result = null;

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT VALUE FROM SCENE_POPERTIES WHERE NAME=?;")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(1);
                }
            }
        }

        return result;
    }

    public void setSceneLength(double length) throws SQLException {
        setSceneProperty(SCENE_PROPERTY_LENGTH, length + "");
    }

    public double getSceneLength() throws SQLException {
        String sLength = getSceneProperty(SCENE_PROPERTY_LENGTH);

        if (sLength == null) {
            return Double.NaN;
        }

        return Double.parseDouble(sLength);
    }

    public void setSceneWidth(double width) throws SQLException {
        setSceneProperty(SCENE_PROPERTY_WIDTH, width + "");
    }

    public double getSceneWidth() throws SQLException {
        String sWidth = getSceneProperty(SCENE_PROPERTY_WIDTH);

        if (sWidth == null) {
            return Double.NaN;
        }

        return Double.parseDouble(sWidth);
    }

    public void setSceneHeight(double height) throws SQLException {
        setSceneProperty(SCENE_PROPERTY_HEIGHT, height + "");
    }

    public double getSceneHeight() throws SQLException {
        String sHeight = getSceneProperty(SCENE_PROPERTY_HEIGHT);

        if (sHeight == null) {
            return Double.NaN;
        }

        return Double.parseDouble(sHeight);
    }

    public void setTileBounds(int bounds[]) throws SQLException {
        JsonArray array = new JsonArray();
        for (int i = 0; i < bounds.length; i++) {
            array.add(new JsonPrimitive(bounds[i]));
        }
        setSceneProperty(SCENE_PROPERTY_TILE_BOUNDS, array.toString());
    }

    public int[] getTileBounds() throws SQLException {
        String sBounds = getSceneProperty(SCENE_PROPERTY_TILE_BOUNDS);

        if (sBounds == null) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(sBounds).getAsJsonArray();
        int result[] = new int[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsInt();
        }

        return result;
    }

    public int getTilesCount() throws SQLException {
        int[] tileBounds = SceneManager.getTileBounds();

        return SceneManager.getTilesCount(tileBounds);
    }

    public synchronized void remarkUnrenderedTiles() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("UPDATE TILES SET TILE=NULL WHERE TILE='';")) {
            ps.executeUpdate();
        }
    }

    public synchronized TileRequest markTileToRender() throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM TILES WHERE TILE IS NULL LIMIT 1;")) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long turn = rs.getLong("TURN");
                    long frame = rs.getLong("FRAME");
                    int x = rs.getInt("X");
                    int y = rs.getInt("Y");

                    try (PreparedStatement ps2 = getConnection().prepareStatement("UPDATE TILES SET TILE=? WHERE X=? AND Y=? AND TURN=? AND FRAME=?;")) {
                        ps2.setBytes(1, new byte[]{});
                        ps2.setInt(2, x);
                        ps2.setInt(3, y);
                        ps2.setLong(4, turn);
                        ps2.setLong(5, frame);

                        ps2.executeUpdate();

                        return new TileRequest(x, y, turn, frame);
                    }

                }
            }

        }

        return null;
    }

    public void removeTiles(long turn, long frame) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM TILES WHERE TURN=? AND FRAME=?;")) {
            ps.setLong(1, turn);
            ps.setLong(2, frame);
            ps.execute();
        }
    }

    public void removeOldTiles(long turnBefore) throws SQLException {
        long currentTurn = getCurrentTurn();
        try (PreparedStatement ps = getConnection().prepareStatement("DELETE FROM TILES WHERE TURN<?;")) {
            ps.setLong(1, currentTurn - turnBefore);
            ps.execute();
        }
    }

    public int getUnrenderedTilesCount() throws SQLException {

        try (PreparedStatement ps = getConnection().prepareStatement("SELECT COUNT(*) AS C FROM TILES WHERE TILE IS NULL OR TILE = '';")) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);

                }
            }

        }

        return 0;
    }

    public void setCurrentTurnAndFrame(long turn, long frame) throws SQLException {

        JsonObject json = new JsonObject();
        json.add(SCENE_PROPERTY_KEY_TURN, new JsonPrimitive(turn));
        json.add(SCENE_PROPERTY_KEY_FRAME, new JsonPrimitive(frame));

        setSceneProperty(SCENE_PROPERTY_CURRENT_TURN_FRAME, json.toString());
    }

    public long[] getCurrentTurnAndFrame() throws SQLException {
        String sBounds = getSceneProperty(SCENE_PROPERTY_CURRENT_TURN_FRAME);

        if (sBounds == null) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(sBounds).getAsJsonObject();

        return new long[]{
            json.get(SCENE_PROPERTY_KEY_TURN).getAsLong(),
            json.get(SCENE_PROPERTY_KEY_FRAME).getAsLong()
        };
    }

    public long getCurrentTurn() throws SQLException {

        long[] result = getCurrentTurnAndFrame();

        if (result == null) {
            return -1;
        }

        return result[0];
    }

    public long getCurrentFrame() throws SQLException {

        long[] result = getCurrentTurnAndFrame();

        if (result == null) {
            return -1;
        }

        return result[1];
    }

}
