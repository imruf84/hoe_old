package hoe.servlets;

import java.io.Serializable;

public class RenderTilesRequest implements Serializable {
    private long turn;
    private long frame;

    public RenderTilesRequest(long turn, long frame) {
        this.turn = turn;
        this.frame = frame;
    }

    public long getTurn() {
        return turn;
    }

    public long getFrame() {
        return frame;
    }
}
