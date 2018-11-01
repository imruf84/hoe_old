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
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

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
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("image/jpeg");
            ImageIO.write(image, "jpg", response.getOutputStream());

        }

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

}
