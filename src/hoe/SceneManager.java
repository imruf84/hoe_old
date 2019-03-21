package hoe;

import hoe.servlets.TileRequest;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import javax.imageio.ImageIO;

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

    public static void updateTile(long turn, long frame, int x, int y, BufferedImage image) throws SQLException, IOException {

        byte[] imageByteArray = null;
        if (image != null) {
            imageByteArray = Compression.compress(Compression.imageToByteArray(image));
        }
        getSceneDataBase().updateTile(turn, frame, x, y, imageByteArray);
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

    public static void addMeteor(Meteor m) throws SQLException {
        getSceneDataBase().storeMeteor(m);
    }

    public static boolean getMeteor(Meteor m) throws SQLException {
        return getSceneDataBase().getMeteor(m);
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

        setCurrentTurnAndFrame(-1, -1);
        setSceneLength(300);
        setSceneWidth(100);
        setSceneHeight(1000);
        setTileBounds(new int[]{-10, 10, -10, 10});
        //setTileBounds(new int[]{0, 0, 0, 0});

        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
