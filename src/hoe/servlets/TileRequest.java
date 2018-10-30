package hoe.servlets;

import java.io.Serializable;

public class TileRequest implements Serializable {

    private final int x;
    private final int y;
    private final int turn;

    public TileRequest(int x, int y, int turn) {
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

    public int getTurn() {
        return turn;
    }

    @Override
    public String toString() {
        return "TileAction{" + "x=" + x + ", y=" + y + ", turn=" + turn + '}';
    }

}
