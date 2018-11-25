package prototype;

import physics.Vector3D;

public abstract class AbstractSegment {

    private Vector3D[] curve;
    private double[] curveDis;
    private int ncurve;
    private double totalDistance;

    public abstract Vector3D pointAt(double t);

    public final void init(double approxlength) {

        // subdivide the curve 
        this.ncurve = (int) (approxlength / 4) + 2;
        this.curve = new Vector3D[ncurve];
        for (int i = 0; i < ncurve; i++) {
            curve[i] = pointAt(i / (float) (ncurve - 1));
        }

        // find the distance of each point from the previous point 
        this.curveDis = new double[ncurve];
        this.totalDistance = 0;
        for (int i = 0; i < ncurve; i++) {
            curveDis[i] = (i == 0) ? 0 : curve[i].cpy().sub(curve[i - 1]).len();
            totalDistance += curveDis[i];
        }
    }

    public Vector3D[] getCurvePoint() {
        return curve;
    }

    public double [] getCurveDistances() {
        return curveDis;
    }

    public int getCurvesCount() {
        return ncurve;
    }

    public double totalDistance() {
        return totalDistance;
    }
}
