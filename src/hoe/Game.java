package hoe;

import java.io.IOException;
import java.sql.SQLException;

public class Game {

    public static final String INIT = "init";
    public static final String GENERATE = "generating";
    public static final String WAIT = "waiting";
    public static final String SIMULATE = "simulating";

    public static String currentState = "";

    public static void init() throws SQLException, IOException {
        Log.info("Init game...");
        setState(INIT);
        start();
    }

    public static void start() throws SQLException, IOException {
        Log.info("Starting game...");

        SceneManager.generate();

        // Kezdődhet a játék.
        setState(WAIT);
    }

    public static void setState(String state) throws IOException {

        if (state.toUpperCase().equals(getCurrentState().toUpperCase())) {
            return;
        }

        currentState = state;
        Log.debug("Game state is changed to: " + state);

        // Felhasználók tájékoztatása.
        UserManager.sendMessageToAll(getStateChangedMessage());
    }

    public static String getStateChangedMessage() {
        return "{\"a\":\"gsc\",\"d\":{\"state\":\"" + getCurrentState() + "\"}}";
    }

    public static String getCurrentState() {
        return currentState;
    }
}
