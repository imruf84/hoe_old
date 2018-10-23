package hoe.servlets;

import hoe.servers.GameServer;
import hoe.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class TileServlet extends HttpServletWithUserValidator {

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        if (null == user) {
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return;
        }

        int coords[] = {0, 0};
        int turn = 0;

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
                    case 4:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        coords[1] = Integer.parseInt(saTileCoords[2]);
                        turn = Integer.parseInt(saTileCoords[3]);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }
/*
        File image = new File("assets/tiles/tile_" + coords[0] + "_" + coords[1] + "_" + turn + ".jpg");

        if (!image.exists()) {
            return;
        }
*/
        response.reset();
        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
//        response.setContentType(getServletContext().getMimeType(image.getName()));
        //response.setHeader("Content-Length", String.valueOf(image.length()));
        response.setHeader("Location", "http://192.168.0.24:8083/render");
        //response.setHeader("Location", "http://localhost:8083/render");
        //response.setHeader("Location", "https://static1.squarespace.com/static/53ebf129e4b0b62422de7a7a/t/578421abe3df287846917de6/1468277166715/?format=500w");
//        Files.copy(image.toPath(), response.getOutputStream());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, GameServer.GET_REQUEST);
    }

    @Override
    protected String getDefaultPagePath() {
        return "";
    }

}
