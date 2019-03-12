package hoe;

import hoe.servlets.ContentServlet;
import hoe.servlets.TileRequest;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class SceneManager {

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

    public static void updateTile(long turn, long frame, int x, int y, BufferedImage image) throws SQLException {
        String base64Image = null;
        if (image != null) {
            base64Image = imgToBase64String(image, ContentServlet.TILE_IMAGE_FORMAT);
        }
        getSceneDataBase().updateTile(turn, frame, x, y, base64Image);
    }
    
    public static void storeTile(long turn, long frame, int x, int y, BufferedImage image) throws SQLException {
        String base64Image = null;
        if (image != null) {
            base64Image = imgToBase64String(image, ContentServlet.TILE_IMAGE_FORMAT);
        }
        getSceneDataBase().storeTile(turn, frame, x, y, base64Image);
    }

    public static BufferedImage getTile(long turn, long frame, int x, int y) throws SQLException {
        String base64Image = getSceneDataBase().getTile(turn, frame, x, y);

        if (base64Image == null) {
            return null;
        }

        return base64StringToImg(base64Image);
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            Log.error(ioe);
        }

        return null;
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            Log.error(ioe);
        }
        return null;
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

    public static TileRequest markTileToRender() throws SQLException {
        return getSceneDataBase().markTileToRender();
    }
    
    public static int getUnrenderedTilesCount() throws SQLException {
        return getSceneDataBase().getUnrenderedTilesCount();
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

        setSceneLength(300);
        setSceneWidth(100);
        setSceneHeight(1000);
        setTileBounds(new int[]{-5, 5, -3, 3});

        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
