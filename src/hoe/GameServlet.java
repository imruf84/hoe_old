package hoe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hoe.editor.TimeElapseMeter;
import hoe.servers.AbstractServer;
import hoe.servers.GameServer;
import hoe.servers.RedirectServer;
import hoe.servlets.GameAction;
import hoe.servlets.HttpServletWithEncryption;
import hoe.servlets.RenderTilesRequest;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
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

    public static final int TURN_LENGTH_IN_SECONDS = 1;
    public static final int RENDER_FPS = 1;
    public static final int TURN_TILES_FRAMES_COUNT = TURN_LENGTH_IN_SECONDS * RENDER_FPS;
    public static final double TURN_TILES_FRAME_DELAY = 1d / (double) RENDER_FPS;
    public static final int TIME_TO_WAIT_TO_CHECK_RENDERING = 1000;

    private static String currentState = "NaN";
    private static GameServer server = null;
    private static Timer tileRenderRemainingTimer = null;
    private static final int PHYSICS_FPS = 100;
    private static final double PHYSICS_DELTA_TIME = 1d / (double) PHYSICS_FPS;
    private static double renderDeltaTime = TURN_TILES_FRAME_DELAY;
    private static double currentGlobalGameTime;
    private static TimeElapseMeter timeElapseMeter = null;

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
        setStateToWait();
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

        if (!isStateInit() && !isStateGenerate()) {
            UserManager.sendMessageToAll(getStateChangedMessage());
        }
    }

    public static boolean isStateInit() {
        return getCurrentState().equals(GAME_STATE_INIT);
    }

    public static void setStateToInit() throws IOException {
        setState(GAME_STATE_INIT);
    }

    public static boolean isStateGenerate() {
        return getCurrentState().equals(GAME_STATE_GENERATE);
    }

    public static void setStateToGenerate() throws IOException {
        setState(GAME_STATE_GENERATE);
    }

    public static boolean isStateRender() {
        return getCurrentState().equals(GAME_STATE_RENDER);
    }

    public static void setStateToRender() throws IOException {

        try {
            long currentTurn = SceneManager.getCurrentTurn();
            long currentFrame = SceneManager.getCurrentFrame();

            if (currentFrame == 0 && isStateRender()) {
                // If all the frames have been rendered we set the gamestate to wait.
                currentTurn++;
                SceneManager.setCurrentTurnAndFrame(currentTurn, currentFrame);
                SceneManager.removeOldTiles(SceneManager.TURNS_TO_KEEP_OLD_TILES);

                // Stop the timer.
                if (tileRenderRemainingTimer != null) {
                    tileRenderRemainingTimer.cancel();
                    tileRenderRemainingTimer = null;
                }

                Log.debug("Stop rendering turn: " + currentTurn);
                timeElapseMeter.stop();

                setStateToWait();
                return;
            }

            if (!isStateRender()) {
                Log.debug("Start rendering turn: " + (currentTurn + 1));
                setState(GAME_STATE_RENDER);
                timeElapseMeter = new TimeElapseMeter();
            }

            if (currentTurn < 0) {
                renderDeltaTime = 0;
            } else {
                renderDeltaTime = TURN_TILES_FRAME_DELAY;
            }

            // Simulating the scene by several steps (depending on the fps)
            Log.debug("Simulating physics...");
            while (renderDeltaTime > 0) {
                renderDeltaTime -= PHYSICS_DELTA_TIME;
                currentGlobalGameTime += PHYSICS_DELTA_TIME;
            }

            SceneManager.setCurrentGlobalGameTime(currentGlobalGameTime);
            Log.debug("Current game time: " + currentGlobalGameTime);

            // Rendering the next frame.
            currentFrame++;
            if (currentFrame == TURN_TILES_FRAMES_COUNT) {
                currentFrame = 0;
                SceneManager.setCurrentTurnAndFrame(currentTurn, currentFrame);
                currentTurn++;
            } else {
                SceneManager.setCurrentTurnAndFrame(currentTurn, currentFrame);
            }

            // Rendering the next turn frame.
            int[] tileBounds = SceneManager.getTileBounds();
            int tileFromX = tileBounds[0];
            int tileToX = tileBounds[1];
            int tileFromY = tileBounds[2];
            int tileToY = tileBounds[3];

            currentTurn = Math.max(0, currentTurn);

            Log.debug("Start rendering frame: " + currentFrame);

            // Create empty frames to fill it by render servers if there are no unfinished tiles...
            if (SceneManager.getUnrenderedTilesCount() == 0) {
                for (int x = tileFromX; x <= tileToX; x++) {
                    for (int y = tileFromY; y <= tileToY; y++) {
                        SceneManager.storeTile(currentTurn, currentFrame, x, y, null);
                    }
                }
            } else {
                SceneManager.remarkUnrenderedTiles();
            }

            // Send the rendering request.
            sendRequestToRedirectServer(GameServer.DO_RENDER_PATH, new RenderTilesRequest(currentTurn, currentFrame));

            if (tileRenderRemainingTimer == null) {
                tileRenderRemainingTimer = new Timer();
                tileRenderRemainingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            int tilesLeft = SceneManager.getUnrenderedTilesCount();
                            if (!(tilesLeft > 0)) {
                                setStateToRender();
                            } else {

                                Log.debug(tilesLeft + " tiles left... [" + timeElapseMeter.getTime() + "ms]");
                                UserManager.sendMessageToAll(getProgressChangedMessage());
                            }
                        } catch (SQLException | IOException ex) {
                            Log.debug("Tiles rendering failed");
                            try {
                                setStateToError();
                            } catch (IOException ex1) {
                                Log.error(ex1);
                            }
                            Log.error(ex);
                            tileRenderRemainingTimer.cancel();
                            tileRenderRemainingTimer = null;
                        }
                    }
                }, 0, TIME_TO_WAIT_TO_CHECK_RENDERING);
            }

        } catch (SQLException ex) {
            Log.error(ex);
            setStateToError();
        }
    }

    public static boolean isStateError() {
        return getCurrentState().equals(GAME_STATE_ERROR);
    }

    public static void setStateToError() throws IOException {
        setState(GAME_STATE_ERROR);
    }

    public static boolean isStateWait() {
        return getCurrentState().equals(GAME_STATE_WAIT);
    }

    public static void setStateToWait() throws IOException {
        try {

            long currentTurn = SceneManager.getCurrentTurn();
            long currentFrame = SceneManager.getCurrentFrame();

            if (currentTurn < 0 || currentFrame != 0 || SceneManager.getUnrenderedTilesCount() > 0) {
                setStateToRender();
            } else {
                setState(GAME_STATE_WAIT);
            }
        } catch (SQLException ex) {
            Log.error(ex);
            setStateToError();
        }

    }

    protected static <T extends Serializable> int sendRequestToRedirectServer(String path, T request) throws IOException {
        String era = RedirectAction.createAndEncrypt(path, null, request);

        String redirectUrl = getGameServer().getRedirectServerUrl() + RedirectServer.REDIRECT_SERVLET_PATH + era;
        HttpClient client = new HttpClient();
        return client.sendGet(redirectUrl);
    }

    public static String getStateChangedMessage() {
        JsonObject json = new JsonObject();
        json.add("a", new JsonPrimitive("gsc"));

        JsonObject data = new JsonObject();
        json.add("d", data);
        data.add("state", new JsonPrimitive(getCurrentState()));

        JsonObject scene = new JsonObject();
        data.add("scene", scene);
        JsonArray tileBounds = new JsonArray();

        try {
            for (int bound : SceneManager.getTileBounds()) {
                tileBounds.add(new JsonPrimitive(bound));
            }

            scene.add("currentTurn", new JsonPrimitive(SceneManager.getCurrentTurn()));

        } catch (SQLException ex) {
            Log.error(ex);
        }

        scene.add("tileBounds", tileBounds);

        return json.toString();
    }

    public static String getProgressChangedMessage() throws SQLException {
        JsonObject json = new JsonObject();
        json.add("a", new JsonPrimitive("prgc"));

        JsonObject data = new JsonObject();
        json.add("d", data);
        double progress = getCurrentProgressPercentage();
        data.add("progress", new JsonPrimitive(progress));

        if (progress > 0) {
            long time = timeElapseMeter.getTime();
            long t = (long) (((double) time / progress) * (1d - progress));
            data.add("time", new JsonPrimitive(Log.formatInterval(t, true)));
        } else {
            data.add("time", new JsonPrimitive("??:??"));
        }

        return json.toString();
    }

    public static double getCurrentProgressPercentage() throws SQLException {
        int tilesLeft = SceneManager.getUnrenderedTilesCount();
        long ct = SceneManager.getCurrentTurn();
        long cf = SceneManager.getCurrentFrame();
        long totalProgressSteps;
        long currentProgressSteps;
        if (ct < 0) {
            totalProgressSteps = SceneManager.getTilesCount();
            currentProgressSteps = totalProgressSteps - tilesLeft;
        } else {
            totalProgressSteps = SceneManager.getTilesCount() * TURN_LENGTH_IN_SECONDS * RENDER_FPS;
            /*
             Frame | 0 Wait 1 2 3 | 0 Wait 1 2 3 |...
             Turn  |        0     |        1     |...
             */
            currentProgressSteps = (cf == 0 ? TURN_LENGTH_IN_SECONDS * RENDER_FPS : cf) * SceneManager.getTilesCount() - tilesLeft;
        }

        return (double) (currentProgressSteps) / (double) totalProgressSteps;
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
            // Continue the rendering...

            Log.debug("Stop rendering frame: " + ga.getData());
            setStateToRender();
        } else {
            setStateToError();
        }
    }
}
