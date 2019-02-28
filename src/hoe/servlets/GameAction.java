package hoe.servlets;

import java.io.Serializable;

public class GameAction implements Serializable {
    
    private final String action;

    public GameAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "GameAction{" + "action=" + action + '}';
    }
    
}
