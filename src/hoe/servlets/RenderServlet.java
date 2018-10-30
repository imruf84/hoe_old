package hoe.servlets;

import hoe.servers.AbstractServer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderServlet extends HttpServletWithApiKeyValidator {

    public RenderServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) throws IOException {
        int w = 500;
        int h = 500;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.red);
        g.drawRect(0, 0, w - 1, h - 1);
        g.setColor(Color.yellow);
        for (int i = 0; i < w / 10; i++) {
            g.fillRect(i * 10, i * 10, 10, 10);
            g.fillRect(w - ((i + 1) * 10), i * 10, 10, 10);
        }
        g.dispose();

        response.reset();
        response.setContentType("image/jpeg");
        response.setStatus(HttpServletResponse.SC_OK);
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

}
