package prototype.unused;

public class Hexagon {

    public static double HEX_UNIT_X_DOUBLE = Math.sqrt(3d);
    public static double HEX_UNIT_X = Math.sqrt(3d)/2d;
    public static double HEX_UNIT_Y = 3d/2d;
    
    double[] points;
    
    public Hexagon(double side) {
        points = new double[12];

        int n = 6;
        double alpha = -Math.PI / 2;
        double r = side;
        for (int i = 0; i < n * 2; i++) {
            points[i] = r * Math.cos(alpha);
            i++;
            points[i] = r * Math.sin(alpha);
            alpha += 2 * Math.PI / n;
        }

    }

    public double[] getPoints() {
        return points;
    }
}
