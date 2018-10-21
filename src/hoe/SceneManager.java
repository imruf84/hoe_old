package hoe;

import java.io.IOException;
import java.sql.SQLException;

public class SceneManager {

    private static String dataBaseIP;
    private static SceneDataBase sceneDataBase;

    public static void init() throws ClassNotFoundException, SQLException {
        Log.info("Init scene...");
        connectToDataBase();
    }

    protected static void connectToDataBase() throws ClassNotFoundException, SQLException {
        sceneDataBase = new SceneDataBase(getDataBaseIp());
    }

    public static SceneDataBase getSceneDataBase() {
        return sceneDataBase;
    }

    public static void setDataBaseIp(String ip) {
        dataBaseIP = ip;
    }

    private static String getDataBaseIp() {
        return dataBaseIP;
    }

    public static void addMeteor(Meteor m) throws SQLException {
        getSceneDataBase().storeMeteor(m);
    }

    public static void generate() throws SQLException, IOException {

        Log.info("Generating scene...");
        Game.setState(Game.GENERATE);
        getSceneDataBase().removeAllObjects();

        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
