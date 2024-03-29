package hoe.servlets;

import hoe.RedirectAction;
import hoe.servers.GameServer;
import hoe.User;
import hoe.servers.AbstractServer;
import hoe.servers.RedirectServer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;


public class TileServlet extends HttpServletWithUserValidator {

    public TileServlet(AbstractServer server) {
        super(server);
    }

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        if (null == user) {
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return;
        }

        int coords[] = {0, 0};
        long turn = 0;
        long frame = 0;

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
                        turn = Long.parseLong(saTileCoords[3]);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        response.reset();
        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);

        String era = RedirectAction.createAndEncrypt(GameServer.GET_TILE_PATH, user.getUserName(), new TileRequest(coords[0], coords[1], turn, frame));

        GameServer server = (GameServer) getServer();
        String redirectUrl = server.getRedirectServerUrl() + RedirectServer.REDIRECT_SERVLET_PATH + era;
        response.setHeader("Location", redirectUrl);
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
