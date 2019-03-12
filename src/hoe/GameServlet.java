package hoe;

import hoe.servers.AbstractServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import hoe.servlets.GameAction;
import hoe.servlets.HttpServletWithEncryption;
import hoe.servlets.RenderTilesRequest;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class GameServlet extends HttpServletWithEncryption {

    public static final String GAME_STATE_INIT = "INIT";
    public static final String GAME_STATE_GENERATE = "GENERATE";
    public static final String GAME_STATE_SIMULATE = "SIMULATE";
    public static final String GAME_STATE_RENDER = "RENDER";
    public static final String GAME_STATE_WAIT = "WAIT";
    public static final String GAME_STATE_ERROR = "ERROR";

    private static String currentState = "NaN";

    private static GameServer server = null;

    public GameServlet(AbstractServer server) {
        super(server);
    }

    public static void init(GameServer server) throws SQLException, IOException {
        Log.info("Init game...");
        setGameServer(server);
        setStateToInit();
    }

    public static GameServer getGameServer() {
        return server;
    }

    public static void setGameServer(GameServer server) {
        GameServlet.server = server;
    }

    public static void start() throws SQLException, IOException {
        Log.info("Starting game...");
        SceneManager.generate();
        setStateToRender();
    }

    private static void setState(String state) throws IOException {

        if (state.toUpperCase().equals(getCurrentState().toUpperCase())) {
            return;
        }

        if (state.equals(GAME_STATE_ERROR)) {
            throw new IOException("GAME_STATE_ERROR");
        }

        String oldState = currentState;
        currentState = state;
        Log.debug("Game state is changed from " + oldState + " to " + currentState);

        UserManager.sendMessageToAll(getStateChangedMessage());
    }

    public static void setStateToInit() throws IOException {
        setState(GAME_STATE_INIT);
    }

    public static void setStateToGenerate() throws IOException {
        setState(GAME_STATE_GENERATE);
    }

    public static void setStateToRender() throws IOException {

        // TODO: check if are there anything to render?
        setState(GAME_STATE_RENDER);

        sendRequestToRedirectServer(GameServer.DO_RENDER_PATH, new RenderTilesRequest(0, 0));
    }

    public static void setStateToError() throws IOException {
        setState(GAME_STATE_ERROR);
    }

    public static void setStateToWait() throws IOException {
        setState(GAME_STATE_WAIT);
    }

    protected static <T extends Serializable> int sendRequestToRedirectServer(String path, T request) throws IOException {
        String era = RedirectAction.createAndEncrypt(path, null, request);

        String redirectUrl = getGameServer().getRedirectServerUrl() + RedirectServer.REDIRECT_SERVLET_PATH + era;
        HttpClient client = new HttpClient();
        return client.sendGet(redirectUrl);
    }

    public static String getStateChangedMessage() {
        return "{\"a\":\"gsc\",\"d\":{\"state\":\"" + getCurrentState() + "\"}}";
    }

    public static String getCurrentState() {
        return currentState;
    }

    @Override
    protected <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException {
        response.reset();
        response.setStatus(HttpStatus.OK_200);

        GameAction ga = (GameAction) action;
        if (ga.isTilesRenderingDone()) {
            setStateToWait();
        } else {
            setStateToError();
        }
    }
}
