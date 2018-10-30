package hoe.servlets;

import hoe.HandfulOfEarth;
import hoe.RedirectAction;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class RedirectServlet extends HttpServletWithEncryption {

    public RedirectServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException {

        response.reset();
        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);

        RedirectAction ra = (RedirectAction) action;
        String data = ra.getData();

        RedirectServer server = (RedirectServer) getServer();
        
        // Redirecting to content server.
        switch (ra.getPath()) {
            case GameServer.TILE_PATH:
                //response.setHeader("Location", "http://localhost:" + HandfulOfEarth.DEFAULT_CONTENT_SERVER_PORT + ContentServer.CONTENT_PATH + data);
                response.setHeader("Location", server.getClients().getFirst() + ContentServer.CONTENT_PATH + data);
                return;
        }

        response.reset();
        response.setStatus(HttpStatus.BAD_GATEWAY_502);
    }

}
