package hoe.servlets;

import java.io.Serializable;

public class TileRequest implements Serializable {

    private final int x;
    private final int y;
    private final long turn;
    private final long frame;

    public TileRequest(int x, int y, long turn, long frame) {
        this.x = x;
        this.y = y;
        this.turn = turn;
        this.frame = frame;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getTurn() {
        return turn;
    }

    public long getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        return "TileRequest{" + "x=" + x + ", y=" + y + ", turn=" + turn + ", frame=" + frame + '}';
    }


}
