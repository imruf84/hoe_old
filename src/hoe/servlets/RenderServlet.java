package hoe.servlets;

import hoe.Log;
import hoe.SceneManager;
import hoe.editor.TimeElapseMeter;
import hoe.servers.AbstractServer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderServlet extends HttpServletWithApiKeyValidator {

    public RenderServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) throws IOException {

        TimeElapseMeter timer = new TimeElapseMeter(true);

        Thread t = new Thread(() -> {
            TileRequest tile;

            Log.debug("Rendering tiles...");

            try {
                while ((tile = SceneManager.markTileToRender()) != null) {

                    int w = 500;
                    int h = 500;
                    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
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
                    int x = tile.getX();
                    int y = tile.getY();
                    long turn = tile.getTurn();
                    long frame = tile.getFrame();
                    g.drawString("x=" + x, 10, (int) (fontSize * 1.1));
                    g.drawString("y=" + y, 10, (int) (fontSize * 2.2));
                    g.drawString("t=" + turn, 10, (int) (fontSize * 3.4));
                    g.drawString("f=" + frame, 10, (int) (fontSize * 4.6));
                    g.dispose();
                    try {
                        // Update tile in database.
                        SceneManager.updateTile(turn, frame, x, y, image);
                    } catch (SQLException ex) {
                        Log.error(ex);
                    }
                }
            } catch (SQLException ex) {
                Log.error(ex);

                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            Log.debug("Rendering tiles [" + getServer().getIp() + ":" + getServer().getPort() + "] has been finished [it took " + timer.stopAndGet() + "].");
        });
        t.start();

        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
