package prototype;

import java.util.LinkedList;
import physics.Vector3D;

public class CentripetalCatmullRomCurve {

    private final LinkedList<AbstractSegment> segments = new LinkedList<>();
    private final LinkedList<Vector3D> points = new LinkedList<>();
    private boolean closed = false;

    public CentripetalCatmullRomCurve() {
    }

    public CentripetalCatmullRomCurve(LinkedList<Vector3D> points) {
        for (Vector3D p : points) {
            appendPoint(p);
        }
    }

    public Vector3D pointAt(double t) {
        int segmentIndex = (int) Math.floor(t);
        double tt = t - (double) segmentIndex;

        CentripetalCatmullRomSegment segment = new CentripetalCatmullRomSegment(new Vector3D[]{getPoint(segmentIndex - 1), getPoint(segmentIndex), getPoint(segmentIndex + 1), getPoint(segmentIndex + 2)});
        return segment.pointAt(tt);
    }

    public final void appendPoint(Vector3D p) {
        getPoints().add(p);
    }

    Vector3D getPoint(int index) {
        return getPoints().get(getIndex(index));
    }

    public int getIndex(int i) {

        int n = getPointsCount();

        if (isClosed()) {
            return (i < 0 ? i + n : i) % n;
        }

        return Math.min(Math.max(i, 0), n - 1);

    }

    public LinkedList<AbstractSegment> getSegments() {
        return segments;
    }

    public LinkedList<Vector3D> getPoints() {
        return points;
    }

    public int getPointsCount() {
        return getPoints().size();
    }

    public int getSegmentsCount() {
        if (isClosed()) {
            return getPointsCount();
        }

        return getPointsCount() - 1;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public LinkedList<Vector3D> generatePathPointsBySteps(int steps) {

        LinkedList<Vector3D> result = new LinkedList<>();

        for (int j = 0; j < getSegmentsCount(); j++) {
            for (int i = 0; i <= steps; i++) {
                Vector3D p = pointAt((double) j + (double) i / (double) steps);
                result.add(p);
            }
        }

        return result;
    }

    public LinkedList<Vector3D> generatePathPointsByLength(double length) {
        LinkedList<Vector3D> result = new LinkedList<>();

        // Resolution of t (smaller is more precise).
        double dtInit = .01d;
        // Resolution of segment length (smaller is more precise).
        double ds0 = .1d;
        // Subdivision.
        double subdivide = length / ds0;
        double a = 0d;
        double b = (double) getSegmentsCount();

        double t1 = a;
        double t2;
        double dt = dtInit;
        double eps = .05d;
        int steps = 0;
        int counter = 0;

        Vector3D p1 = pointAt(t1);
        result.add(p1);
        do {
            t2 = t1 + dt;
            Vector3D p2 = pointAt(t2);
            double ds = p2.distance(p1);
            // Variable "steps" is used to avoid infinite loop.
            if ((ds < ds0 - eps || ds > ds0 + eps) && steps < 100) {
                dt = ds0 / ds * dt;
                //System.out.println(dt);
                steps++;
                continue;
            }
            steps = 0;
            t1 = t2;
            if (++counter >= (int) (subdivide)) {
                result.add(p2);
                counter = 0;
            }
            p1 = new Vector3D(p2);
            dt = dtInit;
        } while (t1 < b);

        result.add(pointAt(b));

        return result;
    }

}
