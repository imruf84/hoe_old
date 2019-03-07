package hoe.servlets;

import hoe.Cryptography;
import hoe.HttpClient;
import hoe.Log;
import hoe.RedirectAction;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import hoe.servers.SubscribeRequest;
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
            case GameServer.GET_TILE_PATH:
                String redirectUrl = server.getClients().get(SubscribeRequest.CONTENT_SERVER_TYPE).getFirst() + ContentServer.CONTENT_PATH + data;
                response.setHeader("Location", redirectUrl);
                return;
            case GameServer.DO_RENDER_PATH:

                Log.debug("Start tiles rendering...");
                Log.debug("Tiles rendering finished");

                sendActionToGameServer(GameAction.GAME_ACTION_TILE_RENDER_DONE);

                return;
        }

        response.reset();
        response.setStatus(HttpStatus.BAD_GATEWAY_502);
    }

    private int sendActionToGameServer(String action) throws IOException {

        RedirectServer server = (RedirectServer) getServer();
        String gameServerUrl = server.getClients().get(SubscribeRequest.GAME_SERVER_TYPE).getFirst();
        GameAction ga = new GameAction(action);
        String era = Cryptography.encryptObject(ga);

        HttpClient c = new HttpClient();
        return c.sendGet(gameServerUrl + GameServer.GAME_PATH + era);
    }

}
