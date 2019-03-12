package hoe.servlets;

import hoe.Cryptography;
import hoe.HttpClient;
import hoe.Log;
import hoe.RedirectAction;
import hoe.SceneDataBase;
import hoe.SceneManager;
import hoe.servers.AbstractServer;
import hoe.servers.ContentServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import hoe.servers.RenderServer;
import hoe.servers.SubscribeRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class RedirectServlet extends HttpServletWithEncryption {

    public static final int TIME_TO_WAIT_TO_CHECK_RENDERING = 1000;

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

                try {

                    RenderTilesRequest rtr = Cryptography.decryptObject(data);
                    int[] tileBounds = SceneManager.getTileBounds();

                    long turn = rtr.getTurn();
                    long frame = rtr.getFrame();
                    int tileFromX = tileBounds[0];
                    int tileToX = tileBounds[1];
                    int tileFromY = tileBounds[2];
                    int tileToY = tileBounds[3];

                    // Create empty frames to fill it by render servers...
                    for (int x = tileFromX; x <= tileToX; x++) {
                        for (int y = tileFromY; y <= tileToY; y++) {
                            SceneManager.storeTile(turn, frame, x, y, null);
                        }
                    }

                    // Send render requests.
                    for (String url : server.getClients().get(SubscribeRequest.RENDER_SERVER_TYPE)) {
                        String renderUrl = url + RenderServer.RENDER_PATH;
                        Log.debug("Sending tiles render request to: " + renderUrl + " ...");
                        HttpClient client = new HttpClient();
                        int statusCode = client.sendGet(renderUrl, true);
                        Log.debug("Response for [" + renderUrl + "] is [" + statusCode + "]: " + client.getResponse());
                    }

                    // Waiting...
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                int tilesLeft = SceneManager.getUnrenderedTilesCount();
                                if (!(tilesLeft > 0)) {
                                    Log.debug("Tiles rendering has been finished");
                                    sendActionToGameServer(GameAction.GAME_ACTION_TILE_RENDER_DONE);
                                    timer.cancel();
                                } else {
                                    Log.debug(tilesLeft + " tiles left...");
                                }
                            } catch (SQLException | IOException ex) {
                                Log.debug("Tiles rendering failed");
                                try {
                                    sendActionToGameServer(GameAction.GAME_ACTION_TILE_RENDER_FAILED);
                                } catch (IOException ex1) {
                                }
                                Log.error(ex);
                                timer.cancel();
                            }
                        }
                    }, 0, TIME_TO_WAIT_TO_CHECK_RENDERING);

                } catch (SQLException ex) {
                    Log.error("Unable to get property [" + SceneDataBase.SCENE_PROPERTY_TILE_BOUNDS + "]: ", ex);
                    sendActionToGameServer(GameAction.GAME_ACTION_TILE_RENDER_FAILED);
                }

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
