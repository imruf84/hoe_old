package prototype;

public abstract class CurveType {

    private Vec2f[] curve;
    private float[] curveDis;
    private int ncurve;
    private float totalDistance;

    public abstract Vec2f pointAt(float t);

    public final void init(float approxlength) {

        // subdivide the curve 
        this.ncurve = (int) (approxlength / 4) + 2;
        this.curve = new Vec2f[ncurve];
        for (int i = 0; i < ncurve; i++) {
            curve[i] = pointAt(i / (float) (ncurve - 1));
        }

        // find the distance of each point from the previous point 
        this.curveDis = new float[ncurve];
        this.totalDistance = 0;
        for (int i = 0; i < ncurve; i++) {
            curveDis[i] = (i == 0) ? 0 : curve[i].cpy().sub(curve[i - 1]).len();
            totalDistance += curveDis[i];
        }
    }

    public Vec2f[] getCurvePoint() {
        return curve;
    }

    public float[] getCurveDistances() {
        return curveDis;
    }

    public int getCurvesCount() {
        return ncurve;
    }

    public float totalDistance() {
        return totalDistance;
    }
}
