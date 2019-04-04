package hoe;

import hoe.servlets.TileRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

public class SceneManager {

    public static final long TURNS_TO_KEEP_OLD_TILES = 2;
    
    private static String dataBaseIP;
    private static SceneDataBase sceneDataBase;
    private static boolean inited = false;

    public static void init() throws ClassNotFoundException, SQLException {
        if (inited) {
            return;
        }

        Log.info("Init scene...");
        connectToDataBase();

        inited = true;
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

    public static void updateTile(long turn, long frame, int x, int y, BufferedImage image, long renderTime) throws SQLException, IOException {

        byte[] imageByteArray = null;
        if (image != null) {
            imageByteArray = Compression.compress(Compression.imageToByteArray(image));
        }
        getSceneDataBase().updateTile(turn, frame, x, y, imageByteArray, renderTime);
    }

    public static void storeTile(long turn, long frame, int x, int y, BufferedImage image) throws SQLException, IOException {

        byte[] imageByteArray = null;
        if (image != null) {
            imageByteArray = Compression.compress(Compression.imageToByteArray(image));
        }
        getSceneDataBase().storeTile(turn, frame, x, y, imageByteArray);
    }

    public static BufferedImage getTile(long turn, long frame, int x, int y) throws SQLException, IOException, DataFormatException {

        byte[] imageByteArray = getSceneDataBase().getTile(turn, frame, x, y);

        if (imageByteArray == null) {
            return null;
        }

        return Compression.byteArrayToImage(Compression.decompress(imageByteArray));
    }
    
    public static long getRenderTimeAvg(long turn) throws SQLException {
        return getSceneDataBase().getRenderTimeAvg(turn);
    }

    public static void addMeteor(Meteor m) throws SQLException {
        getSceneDataBase().storeMeteor(m);
    }

    public static boolean getMeteor(Meteor m) throws SQLException {
        return getSceneDataBase().getMeteor(m);
    }

    public static void setCurrentGlobalGameTime(double time) throws SQLException {
        getSceneDataBase().setCurrentGlobalGameTime(time);
    }
    
    public static double getCurrentGlobalGameTime() throws SQLException {
        return getSceneDataBase().getCurrentGlobalGameTime();
    }
    
    public static void setSceneLength(double length) throws SQLException {
        getSceneDataBase().setSceneLength(length);
    }

    public static double getSceneLength() throws SQLException {
        return getSceneDataBase().getSceneLength();
    }

    public static void setSceneWidth(double width) throws SQLException {
        getSceneDataBase().setSceneWidth(width);
    }

    public static double getSceneWidth() throws SQLException {
        return getSceneDataBase().getSceneWidth();
    }

    public static void setSceneHeight(double height) throws SQLException {
        getSceneDataBase().setSceneHeight(height);
    }

    public static double getSceneHeight() throws SQLException {
        return getSceneDataBase().getSceneHeight();
    }

    public static void setTileBounds(int bounds[]) throws SQLException {
        getSceneDataBase().setTileBounds(bounds);
    }

    public static int[] getTileBounds() throws SQLException {
        return getSceneDataBase().getTileBounds();
    }
    
    public static int getTilesCount() throws SQLException {
        return getSceneDataBase().getTilesCount();
    }
    
    public static int getTilesCount(int[] tileBounds) throws SQLException {
        return (tileBounds[1] - tileBounds[0] + 1) * (tileBounds[3] - tileBounds[2] + 1);
    }
    
    public static void remarkUnrenderedTiles() throws SQLException {
        getSceneDataBase().remarkUnrenderedTiles();
    }

    public static TileRequest markTileToRender() throws SQLException {
        return getSceneDataBase().markTileToRender();
    }

    public static void removeTiles(long turn, long frame) throws SQLException {
        getSceneDataBase().removeTiles(turn, frame);
    }
    
    public static void removeOldTiles(long turnBefore) throws SQLException {
        getSceneDataBase().removeOldTiles(turnBefore);
    }

    public static int getUnrenderedTilesCount() throws SQLException {
        return getSceneDataBase().getUnrenderedTilesCount();
    }

    public static void setCurrentTurnAndFrame(long turn, long frame) throws SQLException {
        getSceneDataBase().setCurrentTurnAndFrame(turn, frame);
    }

    public static long getCurrentTurn() throws SQLException {
        return getSceneDataBase().getCurrentTurn();
    }

    public static long getCurrentFrame() throws SQLException {
        return getSceneDataBase().getCurrentFrame();
    }

    public static void clearAll() throws SQLException, IOException {
        Log.info("Clearing scene...");
        getSceneDataBase().removeAllObjects();
        getSceneDataBase().removeAllTiles();
    }

    public static void generate() throws SQLException, IOException {

        GameServlet.setStateToGenerate();
        Log.info("Generating scene...");

        clearAll();

        setCurrentGlobalGameTime(0);
        setCurrentTurnAndFrame(-1, -1);
        setSceneLength(300);
        setSceneWidth(100);
        setSceneHeight(1000);
        int s = 1;
        setTileBounds(new int[]{-4*s, 4*s, -2*s, 2*s});
        //setTileBounds(new int[]{-1, 1, -1, 1});

        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
