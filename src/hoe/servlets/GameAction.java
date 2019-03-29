package hoe.servlets;

import java.io.Serializable;

public class GameAction implements Serializable {
    
    public static final String GAME_ACTION_TILE_RENDER_DONE = "TILES_RENDERING_DONE";
    public static final String GAME_ACTION_TILE_RENDER_FAILED = "TILES_RENDERING_FAILED";
    
    private final String action;
    private final String data;

    public GameAction(String action, String data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }
    
    public boolean isTilesRenderingDone() {
        return getAction().equals(GAME_ACTION_TILE_RENDER_DONE);
    }

    @Override
    public String toString() {
        return "GameAction{" + "action=" + action + '}';
    }
    
}
