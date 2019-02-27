package hoe.servlets;

import hoe.Log;
import hoe.SceneManager;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
            String tileFileName = ContentServer.TILES_DIRECTORY_PATH + turn + "_" + x + "_" + y + "." + TILE_IMAGE_EXTENSION;
            File tileFile = new File(tileFileName);

            BufferedImage image = null;

            // Reading tile from file.
            if (tileFile.exists()) {
                image = ImageIO.read(tileFile);
            } else {
                // Getting the tile from the database.
                try {
                    image = SceneManager.getTile(turn, x, y);
                } catch (SQLException ex) {
                    Log.error(ex);
                }
            }

            if (image == null) {

                // Rendering tile.
                int w = 500;
                int h = 500;
                image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setColor(Color.red);
                g.drawRect(0, 0, w - 1, h - 1);
                g.setColor(Color.white);

                try (InputStream mainFontIn = getClass().getClassLoader().getResourceAsStream("fonts/cour.ttf")) {

                    Font mainFont = Font.createFont(Font.TRUETYPE_FONT, mainFontIn);

                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

                    ge.registerFont(mainFont);
                } catch (IOException | FontFormatException e) {
                    Log.error(e);
                }

                int fontSize = 100;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g.setFont(new Font("Courier New", Font.PLAIN, fontSize));
                g.drawString("x=" + x, 10, (int) (fontSize * 1.1));
                g.drawString("y=" + y, 10, (int) (fontSize * 2.2));
                g.drawString("t=" + turn, 10, (int) (fontSize * 3.4));
                g.dispose();

                try {
                    // Storing tile to database.
                    SceneManager.storeTile(turn, x, y, image);
                } catch (SQLException ex) {
                    Log.error(ex);
                }

            }

            // Save tile to disk if neccessary.
            if (!tileFile.exists()) {
                Log.debug("Saving tile to disk: " + tileFileName);
                ImageIO.write(image, TILE_IMAGE_FORMAT, tileFile);
            }

            response.reset();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(TILE_CONTENT_TYPE);
            ImageIO.write(image, TILE_IMAGE_FORMAT, response.getOutputStream());

        }

    }

}
