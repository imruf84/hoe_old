package prototype;

import java.util.ArrayList;
import java.util.LinkedList;
import physics.Vector3D;

public class CentripetalCatmullRomCurve {

    private final ArrayList<Vector3D> points = new ArrayList<>();
    private boolean closed = false;
    private CentripetalCatmullRomSegment prevSegment = null;
    private int prevSegmentIndex = -1;

    public CentripetalCatmullRomCurve() {
    }

    public CentripetalCatmullRomCurve(LinkedList<Vector3D> points) {
        for (Vector3D p : points) {
            appendPoint(p);
        }
    }

    public CurvePoint pointAt(double t) {

        int segmentIndex = (int) Math.floor(t);

        if (segmentIndex != prevSegmentIndex) {
            prevSegmentIndex = segmentIndex;
            prevSegment = new CentripetalCatmullRomSegment(new Vector3D[]{getPoint(segmentIndex - 1), getPoint(segmentIndex), getPoint(segmentIndex + 1), getPoint(segmentIndex + 2)});;
        }

        double tt = t - (double) segmentIndex;

        return new CurvePoint(prevSegment.pointAt(tt), t);
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

    public ArrayList<Vector3D> getPoints() {
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

    public LinkedList<CurvePoint> generatePathPointsBySteps(int steps) {

        LinkedList<CurvePoint> result = new LinkedList<>();

        for (int j = 0; j < getSegmentsCount(); j++) {
            for (int i = 0; i <= steps; i++) {
                CurvePoint p = pointAt((double) j + (double) i / (double) steps);
                result.add(p);
            }
        }

        return result;
    }

    public CurvePoint getNextPointByLength(double t0, double length) {
        return generatePathPointsByLength(t0, length, 2).getLast();
    }

    public LinkedList<CurvePoint> generatePathPointsByLength(double length) {
        return generatePathPointsByLength(0, length, (int) Double.POSITIVE_INFINITY);
    }

    public LinkedList<CurvePoint> generatePathPointsByLength(double length, int count) {
        return generatePathPointsByLength(0, length, count);
    }

    public LinkedList<CurvePoint> generatePathPointsByLength(double t0, double length, int count) {
        LinkedList<CurvePoint> result = new LinkedList<>();

        // Resolution of t (smaller is more precise).
        double dtInit = .01d;
        // Resolution of segment length (smaller is more precise).
        double ds0 = .01d;
        // Subdivision.
        double subdivide = length / ds0;
        double a = t0;
        double b = (double) getSegmentsCount();

        double t1 = a;
        double t2;
        double dt = dtInit;
        double eps = .02d;
        int steps = 0;
        int counter = 0;

        CurvePoint p1 = pointAt(t1);
        result.add(p1);
        if (result.size() == count) {
            return result;
        }

        do {
            t2 = t1 + dt;
            CurvePoint p2 = pointAt(t2);
            double ds = p2.distance(p1);
            // Variable "steps" is used to avoid infinite loop.
            if ((ds < ds0 - eps || ds > ds0 + eps) && steps < 100) {
                dt = ds0 / ds * dt;
                steps++;
                continue;
            }
            steps = 0;
            t1 = t2;
            if (++counter >= (int) (subdivide)) {
                result.add(p2);
                if (result.size() == count) {
                    return result;
                }
                counter = 0;
            }
            p1 = new CurvePoint(p2);
            dt = dtInit;
        } while (t1 < b);

        if (result.size() < count) {
            result.add(pointAt(b));
        }

        return result;
    }
    
    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

}
