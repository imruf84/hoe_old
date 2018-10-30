package hoe.servlets;

import hoe.Log;
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
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentServlet extends HttpServletWithEncryption {

    public ContentServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException {

        if (action instanceof TileRequest) {
            TileRequest tr = (TileRequest) action;

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
            g.drawString("x=" + tr.getX(), 10, (int) (fontSize * 1.1));
            g.drawString("y=" + tr.getY(), 10, (int) (fontSize * 2.2));
            g.drawString("t=" + tr.getTurn(), 10, (int) (fontSize * 3.4));
            g.dispose();

            response.reset();
            response.setContentType("image/jpeg");
            response.setStatus(HttpServletResponse.SC_OK);
            ImageIO.write(image, "jpg", response.getOutputStream());
            
            return;
        }
        
    }

}
