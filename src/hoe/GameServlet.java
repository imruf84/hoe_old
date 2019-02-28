package hoe;

import hoe.servers.AbstractServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import hoe.servlets.GameAction;
import hoe.servlets.HttpServletWithEncryption;
import hoe.servlets.HttpServletWithUserValidator;
import hoe.servlets.RenderTilesRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class GameServlet extends HttpServletWithEncryption {

    public static final String GAME_STATE_INIT = "INIT";
    public static final String GAME_STATE_GENERATE = "GENERATE";
    public static final String GAME_STATE_SIMULATE = "SIMULATE";
    public static final String GAME_STATE_RENDER = "RENDER";
    public static final String GAME_STATE_WAIT = "WAIT";

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

    public static void setState(String state) throws IOException {

        if (state.toUpperCase().equals(getCurrentState().toUpperCase())) {
            return;
        }

        String oldState = currentState;
        currentState = state;
        Log.debug("Game state is changed from " + oldState + " to " + currentState);

        UserManager.sendMessageToAll(getStateChangedMessage());
    }

    public static void setStateToInit() throws IOException {
        setState(GAME_STATE_INIT);
    }

    public static void setStateToRender() throws IOException {

        // TODO: check it there are anything to render
        setState(GAME_STATE_RENDER);

        RenderTilesRequest ta = new RenderTilesRequest(0, 0);
        String eta = Cryptography.encryptObject(ta);

        RedirectAction ra = new RedirectAction(GameServer.DO_RENDER_PATH, Instant.now().getEpochSecond(), null);
        ra.setData(eta);
        String era = Cryptography.encryptObject(ra);

        String redirectUrl = getGameServer().getRedirectServerUrl() + RedirectServer.REDIRECT_SERVLET_PATH + era;
        HttpClient client = new HttpClient();
        client.sendGet(redirectUrl);
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
        System.out.println(ga.toString());
    }
}
