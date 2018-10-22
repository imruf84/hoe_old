package hoe.servlets;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        for (int i = 0; i < 10; i++) {
            g.fillRect(i * 10, i * 10, 10, 10);
        }
        g.dispose();

        response.reset();
        response.setContentType("image/jpeg");
        response.setStatus(HttpServletResponse.SC_OK);
        ImageIO.write(image, "jpg", response.getOutputStream());

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    }
}
