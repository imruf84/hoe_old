package hoe.servlets;

import java.io.Serializable;

public class TileRequest implements Serializable {

    private final int x;
    private final int y;
    private final long turn;

    public TileRequest(int x, int y, long turn) {
        this.x = x;
        this.y = y;
        this.turn = turn;
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

    @Override
    public String toString() {
        return "TileAction{" + "x=" + x + ", y=" + y + ", turn=" + turn + '}';
    }

}
