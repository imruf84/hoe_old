package prototype;

import java.util.LinkedList;
import physics.Vector3D;

public class Player {

    public static final double STEPS_UNIT_LENGTH = 2d;

    private final String name;
    private final CurvePoint position;
    private final CurvePoint nextPosition;
    private final CurvePoint previousPosition;
    private double orientation = 0;
    private double orientationTo = 0;
    private double orientationMaxSpeed = 5;
    private final Curve path = new Curve();
    private final double radius;
    private final double maxStep;

    public Player(String name, Vector3D position, double radius, double maxStep) {
        this.name = name;
        this.radius = radius;
        this.position = new CurvePoint(position, 0);
        this.previousPosition = new CurvePoint(position, 0);
        this.nextPosition = new CurvePoint(position, 0);
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

    public CurvePoint getPreviousPosition() {
        return previousPosition;
    }

    public void setPosition(CurvePoint p) {
        getPosition().set(p);
    }
    
    public void setNextPosition(CurvePoint p) {
        getPreviousPosition().set(getPosition());
        getNextPosition().set(p);
        updateOrientation();
    }

    public CurvePoint getNextPosition() {
        return nextPosition;
    }

    protected void updateOrientation() {

        if (getPreviousPosition().equals(getPosition())) {
            return;
        }

        Vector3D direction = Vector3D.subtract(getPosition(), getPreviousPosition());

        orientationTo = (-90 + Math.atan2(direction.y, direction.x) * 180 / Math.PI);

        double da = (orientationTo - orientation + 540) % 360 - 180;

        double speed=direction.length();
        double maxanglespeed=10;
        double minanglespeed=5;
        double maxspeed=getMaxStep();
        double minspeed=0;
        
        double x0=minspeed;
        double x1=maxspeed;
        double y0=maxanglespeed;
        double y1=minanglespeed;
        double x=speed;
        double y=(y0*(x1-x)+y1*(x-x0))/(x1-x0);
        
        orientationMaxSpeed = y;
        
        orientation += Math.max(Math.min(da, orientationMaxSpeed), -orientationMaxSpeed);
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

    public double getOrientation() {
        return orientation;
    }

    public CurvePoint getNextPositionOnPath() {

        CurvePoint currentPosOnPath = getPath().pointAt(getPosition().t);

        double length = getMaxStep();

        LinkedList<CurvePoint> nextPoints = getPath().generatePathPointsByLength(getPosition().t, length, 2);
        CurvePoint nextPointOnPath = nextPoints.getLast();

        double tollerance = length * length;

        if (currentPosOnPath.distance(getPosition()) > tollerance) {
            double t = currentPosOnPath.t + (nextPointOnPath.t - currentPosOnPath.t) / 2d;
            return getPath().pointAt(t);
        }

        return nextPointOnPath;
    }

    public void doOneStep(double t) {
        //setPosition(nextPos == null ? getNextPositionOnPath() : nextPos);
        setPosition(new CurvePoint(getPosition().add(Vector3D.subtract(getNextPosition(), getPreviousPosition()).scale(t)),getNextPosition().t));
        updateOrientation();
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
