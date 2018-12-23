package prototype;

import hoe.editor.TimeUtils;
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
    private double orientationMaxSpeed = 0;
    private final Curve path = new Curve();
    private final double radius;
    private final double maxStep;
    public double step;
    double maxOrientationSpeed = 30;
    double minOrientationSpeed = 15;

    public Player(String name, Vector3D position, double radius, double maxStep) {
        this.name = name;
        this.radius = radius;
        this.position = new CurvePoint(position, 0);
        this.previousPosition = new CurvePoint(position, 0);
        this.nextPosition = new CurvePoint(position, 0);
        this.maxStep = maxStep;
        addNavigationPoint();
    }

    public double getOrientationTo() {
        return orientationTo;
    }

    public double getMaxOrientationSpeed() {
        return maxOrientationSpeed;
    }

    public double getMinOrientationSpeed() {
        return minOrientationSpeed;
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
        //updateOrientation();
        calculateNextOrientation();
    }

    public CurvePoint getNextPosition() {
        return nextPosition;
    }

    public boolean isLastNavigationPointReached() {
        return getPosition().getT() >= (double) getPath().getPointsCount() - 1;
    }

    protected void calculateNextOrientation() {

        CurvePoint prev = getPosition();
        CurvePoint next = getNextPosition();
        
        //CurvePoint prev = getPreviousPosition();
        //CurvePoint next = getNextPosition();

        //CurvePoint next = getNextPositionOnPath();
        if (isLastNavigationPointReached()) {
            prev = getPath().pointAt(getPosition().getT() - .01d);
            next = getNextPositionOnPath();
        }

        if (getPreviousPosition().equals(next)) {
            return;
        }
        //Vector3D direction = Vector3D.subtract(next, prev);
        //orientationTo = (-90 + Math.atan2(direction.y, direction.x) * 180 / Math.PI);
        orientationTo = calculateOrientation(prev, next);
    }

    public static double calculateOrientation(Vector3D prev, Vector3D next) {
        Vector3D direction = Vector3D.subtract(next, prev);
        return (-90 + Math.atan2(direction.y, direction.x) * 180 / Math.PI);
    }

    public static double calculateOrientationDifference(double o1, double o2) {
        return (o1 - o2 + 540) % 360 - 180;
    }

    public double getOrientationLeft() {
        //return (orientationTo - orientation + 540) % 360 - 180;
        return calculateOrientationDifference(orientationTo, orientation);
    }

    protected void updateOrientation() {

        if (getPreviousPosition().equals(getPosition()) && !isLastNavigationPointReached()) {
            return;
        }

        Vector3D direction = Vector3D.subtract(getPosition(), getPreviousPosition());

        double da = getOrientationLeft();

        double speed = direction.length();

        if (isLastNavigationPointReached()) {
            speed = getMaxStep();
        }

        double dt = TimeUtils.getDeltaTime();

        double maxspeed = getMaxStep();
        double minspeed = 0;

        double x0 = minspeed;
        double x1 = maxspeed;
        double y0 = getMaxOrientationSpeed() * dt;
        double y1 = getMinOrientationSpeed() * dt;
        double x = speed;
        double y = (y0 * (x1 - x) + y1 * (x - x0)) / (x1 - x0);

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

    public void initPosition() {
        CurvePoint cp = getPath().pointAt(0);
        setPosition(cp);
        setNextPosition(cp);
        orientation = 0;
        orientationTo = 0;
        update();
    }

    public double calculateStep() {
        return calculateStepByOrientation(getPreviousPosition(), getPosition(), maxStep, getOrientation());
    }

    public static double calculateStepByOrientation(Vector3D prev, Vector3D current, double maxStep, double orientation) {
        double dOrientation = Math.abs(Player.calculateOrientationDifference(Player.calculateOrientation(prev, current), orientation));
        //double dOrientation = (Player.calculateOrientationDifference(Player.calculateOrientation(prev, current),orientation));

        return maxStep * (1d - (dOrientation / 180d) * 1d);
    }

    public CurvePoint getNextPositionOnPath() {

        //CurvePoint currentPosOnPath = getPath().pointAt(getPosition().t);
        //double length = getMaxStep();
        //double length = calculateStep();
        step = calculateStep();

        CurvePoint nextPointOnPath = getPath().generatePathPointsByLength(getPosition().t, 0, 2).getLast();
        double dNext = nextPointOnPath.distance(getPosition());

        if (dNext > step) {

            for (int i = 0; i < 10; i++) {
                CurvePoint nextNextPointOnPath = getPath().generatePathPointsByLength(nextPointOnPath.t, Math.min(getRadius() / dNext, step), 2).getLast();
                double dNextNext = nextPointOnPath.distance(nextNextPointOnPath);
                if (dNextNext <= dNext) {
                    nextPointOnPath.set(nextNextPointOnPath);
                    dNext = dNextNext;
                } else {
                    break;
                }
            }
        } else {
            nextPointOnPath = getPath().generatePathPointsByLength(getPosition().t, step, 2).getLast();
        }

        return nextPointOnPath;

        //step = getMaxStep();
/*
        //LinkedList<CurvePoint> nextPoints = getPath().generatePathPointsByLength(getPosition().t, length, 2);
        LinkedList<CurvePoint> nextPoints = getPath().generatePathPointsByLength(getPosition().t, step, 2);
        CurvePoint nextPointOnPath = nextPoints.getLast();
        //double t = nextPointOnPath.getT();
        //double tollerance = getRadius()*getRadius();
        //double tollerance = getRadius()+step;
        //double tollerance = step*step;
        //double tollerance = getMaxStep()*getMaxStep();
        //double tollerance = Math.pow(getRadius()*step,1);
        double tollerance = Math.pow(step/getMaxStep(),1);
        
        

        double da = Math.abs(getOrientationLeft());
        double factor = 1;
        double d = currentPosOnPath.distance(getPosition());
        //factor = Math.max(factor, d/(tollerance*tollerance));
        //factor = Math.max(factor, d/(tollerance));
        factor = Math.max(factor, d/tollerance);
        //factor = Math.max(factor, da / (getMaxOrientationSpeed()*getMaxOrientationSpeed()));
        //factor = Math.max(factor, da / getMaxOrientationSpeed());
        double t = currentPosOnPath.t + (nextPointOnPath.t - currentPosOnPath.t) / factor;
        return getPath().pointAt(t);*/
    }

    public void doOneStep(double t, boolean updateOrientation) {
        setPosition(new CurvePoint(Vector3D.add(getPreviousPosition(), Vector3D.subtract(getNextPosition(), getPreviousPosition()).scale(t)), getNextPosition().t));
        if (updateOrientation) {
            updateOrientation();
        }
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
