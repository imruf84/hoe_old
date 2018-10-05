package hoe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.StatusCode;

/**
 * Csempék letöltését vezérlő szervlet osztálya.
 *
 * @author igalambo
 */
public class TileServlet extends HttpServletWithUserValidator {

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        // Ha a felhasználó nincs hitelesítve akkor nem küldünk semmit.
        if (null == user) {
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return;
        }

        int coords[] = {0, 0};

        try {
            if (request.getPathInfo() != null) {
                String saTileCoords[] = request.getPathInfo().split("/");
                switch (saTileCoords.length) {
                    case 2:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        break;
                    case 3:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        coords[1] = Integer.parseInt(saTileCoords[2]);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        // Fájl megnyitása.
        File image = new File("assets/videos/anim_" + coords[0] + "_" + coords[1] + ".jpg");

        // Ha nem létezik a fájl akkor kilépünk.
        if (!image.exists()) {
            return;
        }

        // Kép küldése.
        response.reset();
        response.setContentType(getServletContext().getMimeType(image.getName()));
        response.setHeader("Content-Length", String.valueOf(image.length()));
        Files.copy(image.toPath(), response.getOutputStream());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, HttpServer.GET_REQUEST);
    }

    @Override
    protected String getDefaultPagePath() {
        return "";
    }

}
