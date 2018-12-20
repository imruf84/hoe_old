package prototype;

import java.util.LinkedList;
import physics.Vector3D;

public class Player {

    public static final double STEPS_UNIT_LENGTH = 2d;

    private final String name;
    private final CurvePoint position;
    private final Curve path = new Curve();
    private final double radius;
    private final double maxStep;

    public Player(String name, Vector3D position, double radius, double maxStep) {
        this.name = name;
        this.radius = radius;
        this.position = new CurvePoint(position, 0);
        this.maxStep = maxStep;
        addNavigationPoint();
    }

    public String getName() {
        return name;
    }

    public double getRadius() {
        return radius;
    }

    public double getMaxStep() {
        return maxStep;
    }

    public Curve getPath() {
        return path;
    }

    public CurvePoint getPosition() {
        return position;
    }

    protected void recalculatePath(Vector3D newPos) {

        // Remove the control points behind the player.
        int n = (int) Math.floor(getPosition().t);
        for (int i = 0; i < n; i++) {
            removeNavigationPoint(0);
        }

        Vector3D lp = getPath().getPoints().get(0);
        lp.set(newPos);
        getPosition().t = 0;
    }

    public void update() {
    }

    public CurvePoint getNextPosition() {

        CurvePoint currentPosOnPath = getPath().pointAt(getPosition().t);

        double length = getMaxStep();

        LinkedList<CurvePoint> nextPoints = getPath().generatePathPointsByLength(getPosition().t, length, 2);
        CurvePoint nextPointOnPath = nextPoints.getLast();

        double tollerance = length * length;

        if (currentPosOnPath.distance(getPosition()) > tollerance) {
            double t = currentPosOnPath.t + (nextPointOnPath.t - currentPosOnPath.t) / 2d;
            return getPath().pointAt(t);
            //return currentPosOnPath;
        }

        return nextPointOnPath;
    }

    public void doOneStep(CurvePoint nextPos) {
        getPosition().set(nextPos == null ? getNextPosition() : nextPos);
        update();
    }

    public void removeNavigationPoint(int i) {
        getPath().getPoints().remove(i);
    }

    private void addNavigationPoint() {
        getPath().appendPoint(new Vector3D(getPosition()));
    }

    public void addNavigationPoint(Vector3D p) {

        Curve c = getPath();
        if (c.isEmpty()) {
            // HACK: origo in global space
            //getPath().appendPoint(new Vector3D());

            addNavigationPoint();
        }

        // HACK: p in global space
        //p.x-=getPosition().x;
        //p.y-=getPosition().y;
        getPath().appendPoint(p);
    }

}
