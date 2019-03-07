package hoe;

import hoe.servlets.ContentServlet;
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

    public static void storeTile(long turn, int x, int y, BufferedImage image) throws SQLException {
        String base64Image = imgToBase64String(image, ContentServlet.TILE_IMAGE_FORMAT);
        getSceneDataBase().storeTile(turn, x, y, base64Image);
    }

    public static BufferedImage getTile(long turn, int x, int y) throws SQLException {
        String base64Image = getSceneDataBase().getTile(turn, x, y);

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
    
    public static void generate() throws SQLException, IOException {

        GameServlet.setStateToGenerate();
        Log.info("Generating scene...");
        getSceneDataBase().removeAllObjects();

        new Meteor(1, 2, "imruf84", 3, 4, 5, 6, 7).storeToDataBase().getID();
    }
}
