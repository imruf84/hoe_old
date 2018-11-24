package prototype;

import java.util.LinkedList;

public class CCRCurve {

    private LinkedList<CurveType> segments = new LinkedList<>();
    private LinkedList<Vec2f> points = new LinkedList<>();

    public CCRCurve() {
    }

    public CCRCurve(LinkedList<Vec2f> points) {
        for (Vec2f p : points) {
            appendPoint(p);
        }
    }

    public Vec2f pointAt(float t) {
        int segmentIndex = (int) Math.floor(t);
        float tt = t - (float) segmentIndex;

        CentripetalCatmullRom segment = new CentripetalCatmullRom(new Vec2f[]{getPoint(segmentIndex - 1), getPoint(segmentIndex), getPoint(segmentIndex + 1), getPoint(segmentIndex + 2)});
        return segment.pointAt(tt);
    }

    public final void appendPoint(Vec2f p) {
        getPoints().add(p);
    }

    Vec2f getPoint(int index) {
        return getPoints().get(getIndex(index));
    }

    public int getIndex(int i) {

        int n = getPointsCount();

        if (isClosed()) {
            return (i < 0 ? i + n : i) % n;
        }

        return Math.min(Math.max(i, 0), n - 1);

    }

    public LinkedList<CurveType> getSegments() {
        return segments;
    }

    public LinkedList<Vec2f> getPoints() {
        return points;
    }

    public int getPointsCount() {
        return getPoints().size();
    }

    private boolean isClosed() {
        return false;
    }
}
