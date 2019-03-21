package hoe.servlets;

import hoe.Log;
import hoe.SceneManager;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.DataFormatException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentServlet extends HttpServletWithEncryption {

    public static final String TILE_IMAGE_EXTENSION = "jpg";
    public static final String TILE_IMAGE_FORMAT = "jpg";
    public static final String TILE_CONTENT_TYPE = "image/jpeg";

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
            File tileFile = new File(tileFileName);

            BufferedImage image = null;

            // Reading tile from file.
            if (tileFile.exists()) {
                image = ImageIO.read(tileFile);
            } else {
                // Getting the tile from the database.
                try {
                    image = SceneManager.getTile(turn, frame, x, y);
                } catch (DataFormatException | SQLException ex) {
                    Log.error(ex);
                }
            }

            // Save tile to disk if neccessary.
            if (!tileFile.exists()) {
                //Log.debug("Saving tile to disk: " + tileFileName);
                ImageIO.write(image, TILE_IMAGE_FORMAT, tileFile);
            }

            response.reset();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(TILE_CONTENT_TYPE);
            ImageIO.write(image, TILE_IMAGE_FORMAT, response.getOutputStream());

        }

    }

}
