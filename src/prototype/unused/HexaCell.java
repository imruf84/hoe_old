package prototype.unused;

public class HexaCell {

    public static final double HEXA_CELL_SIDE = 50d;

    private final int coords[];
    private final double position[];
    private final double side;

    public HexaCell(int x, int y) {
        this(x, y, HEXA_CELL_SIDE);
    }

    public HexaCell(int x, int y, double side) {
        this.side = side;
        coords = new int[]{x, y};
        position = new double[]{x * side * Math.sqrt(3d), y * side * 3d / 2d};
        if (y % 2 != 0) {
            position[0] += side * Math.sqrt(3d) / 2d;
        }
    }

    public double getSide() {
        return side;
    }

    public int[] getCoords() {
        return coords;
    }

    public double[] getPosition() {
        return position;
    }

}
