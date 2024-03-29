package hoe.servlets;

import hoe.Log;
import hoe.SceneManager;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentServlet extends HttpServletWithEncryption {

    public static final String TILE_IMAGE_EXTENSION = "png";
    public static final String TILE_IMAGE_FORMAT = "png";
    public static final String TILE_CONTENT_TYPE = "image/png";
    public static final float TILE_COMPRESSION_QUALITY = 1f;
    public static final boolean TILE_CACHE_ENABLED = true;
    private final Object tileFileLock = new Object();

    public ContentServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException {

        if (action instanceof TileRequest) {
            TileRequest tr = (TileRequest) action;

            int x = tr.getX();
            int y = tr.getY();
            long turn = tr.getTurn();
            long frame = tr.getFrame();
            String tileFileName = ContentServer.TILES_CACHE_PATH + turn + "_" + x + "_" + y + "." + TILE_IMAGE_EXTENSION;

            BufferedImage image = null;

            if (TILE_CACHE_ENABLED) {
                File tileFile = new File(tileFileName);

                // TODO: find a better solution (e.g. hashmap with the saved tiles)
                synchronized (tileFileLock) {
                    // Reading tile from file.
                    if (tileFile.exists()) {
                        image = ImageIO.read(tileFile);
                    } else {
                        // Getting the tile from the database.
                        try {
                            image = SceneManager.getTile(turn, frame, x, y);

                            // Save tile to disk if neccessary.
                            ImageIO.write(image, TILE_IMAGE_FORMAT, tileFile);

                            // Remove old files.
                            removeOldTiles(turn);

                        } catch (DataFormatException | SQLException ex) {
                            Log.error(ex);
                        }
                    }
                }
            } else {
                try {
                    image = SceneManager.getTile(turn, frame, x, y);
                } catch (SQLException | DataFormatException ex) {
                    Log.error(ex);
                }
            }

            response.reset();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(TILE_CONTENT_TYPE);
            ImageIO.write(image, TILE_IMAGE_FORMAT, response.getOutputStream());

        }

    }

    // TODO: schedule this task instead of call on every tile saving
    private void removeOldTiles(long turn) {
        // TODO: remove all the tiles which are < turn...
        for (File f : new File(ContentServer.TILES_CACHE_PATH).listFiles()) {
            if (f.getName().startsWith("" + (turn - SceneManager.TURNS_TO_KEEP_OLD_TILES - 1))) {
                f.delete();
            }
        }
    }

}
