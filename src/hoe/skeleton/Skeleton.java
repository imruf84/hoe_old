package hoe.skeleton;

import hoe.nonlinear.Calcfc;
import hoe.nonlinear.Cobyla;
import hoe.nonlinear.CobylaExitStatus;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import org.joml.Intersectiond;
import org.joml.Vector3d;

public class Skeleton {

    private final JointChain leftArm = new JointChain();
    private final JointChain rightArm = new JointChain();
    private double shouldersDistanceGoal;
    private final int iterations = 5;

    public Skeleton() {
        leftArm.appendJoint(new Joint(5, 90 + 30));
        leftArm.appendJoint(new Joint(5, 70));
        leftArm.appendJoint(new Joint(5, 20));
        updatePositions();
        leftArm.setTarget(leftArm.getEndPosition());

        rightArm.appendJoint(new Joint(5, 90 - 30));
        rightArm.appendJoint(new Joint(5, -70));
        rightArm.appendJoint(new Joint(5, -20));
        updatePositions();
        rightArm.setTarget(rightArm.getEndPosition());

        setShouldersDistanceGoal(getLeftArm().getJoint(0).getTail().distance(getRightArm().getJoint(0).getTail()));
    }

    public double getShouldersDistanceGoal() {
        return shouldersDistanceGoal;
    }

    public final void setShouldersDistanceGoal(double shouldersDistanceGoal) {
        this.shouldersDistanceGoal = shouldersDistanceGoal;
    }

    public final JointChain getLeftArm() {
        return leftArm;
    }

    public final JointChain getRightArm() {
        return rightArm;
    }

    public Vector3d getLeftShoulderPosition() {
        return getLeftArm().getJoint(0).getTail();
    }

    public Vector3d getRightShoulderPosition() {
        return getRightArm().getJoint(0).getTail();
    }

    public Vector3d getLeftElbowPosition() {
        return getLeftArm().getJoint(1).getTail();
    }

    public Vector3d getRightElbowPosition() {
        return getRightArm().getJoint(1).getTail();
    }

    public Vector3d getLeftWristPosition() {
        return getLeftArm().getJoint(2).getTail();
    }

    public Vector3d getRightWristPosition() {
        return getRightArm().getJoint(2).getTail();
    }

    public double getShouldersDistance() {
        return getLeftShoulderPosition().distance(getRightShoulderPosition());
    }

    private void updatePositions() {
        getLeftArm().updatePositions();
        getRightArm().updatePositions();
    }

    public void render(Graphics2D g2d, double scale) {

        g2d.setStroke(new BasicStroke(2f));

        // arms
        g2d.setColor(Color.green);
        LinkedList<Vector3d> positions = getLeftArm().getPositions(true);
        for (int i = 0; i < positions.size() - 1; i++) {
            Vector3d p0 = positions.get(i);
            Vector3d p1 = positions.get(i + 1);
            g2d.drawLine((int) (p0.x * scale), (int) (p0.y * scale), (int) (p1.x * scale), (int) (p1.y * scale));
        }

        g2d.setColor(Color.blue);
        positions = getRightArm().getPositions(true);
        for (int i = 0; i < positions.size() - 1; i++) {
            Vector3d p0 = positions.get(i);
            Vector3d p1 = positions.get(i + 1);
            g2d.drawLine((int) (p0.x * scale), (int) (p0.y * scale), (int) (p1.x * scale), (int) (p1.y * scale));
        }

        // shoulders distance
        g2d.setColor(Color.orange);
        Vector3d p0 = getLeftShoulderPosition();
        Vector3d p1 = getRightShoulderPosition();
        g2d.drawLine((int) (p0.x * scale), (int) (p0.y * scale), (int) (p1.x * scale), (int) (p1.y * scale));

        // targets
        int s = 6;
        Vector3d t = getLeftArm().getTarget();
        g2d.setColor(Color.yellow);
        g2d.drawLine((int) (t.x * scale - s), (int) (t.y * scale), (int) (t.x * scale + s), (int) (t.y * scale));
        g2d.drawLine((int) (t.x * scale), (int) (t.y * scale - s), (int) (t.x * scale), (int) (t.y * scale + s));

        t = getRightArm().getTarget();
        g2d.setColor(Color.yellow);
        g2d.drawLine((int) (t.x * scale - s), (int) (t.y * scale), (int) (t.x * scale + s), (int) (t.y * scale));
        g2d.drawLine((int) (t.x * scale), (int) (t.y * scale - s), (int) (t.x * scale), (int) (t.y * scale + s));
    }

    public void solveDistanceConstraint(Vector3d v1, Vector3d v2, double d) {
        Vector3d delta = v1.sub(v2, new Vector3d());
        double deltalength = delta.length();

        double diff = (deltalength - d) / deltalength;
        delta = delta.mul(.5d * diff);

        v1.sub(delta);
        v2.add(delta);
    }

    public double distancePointAndPlane(Vector3d A, Vector3d B, Vector3d C, Vector3d P) {
        Vector3d AB = B.sub(A, new Vector3d());
        Vector3d AC = C.sub(A, new Vector3d());
        Vector3d N = AB.cross(AC).normalize();
        double D = -N.dot(A);
        double d = Math.abs(N.dot(P) + D);

        return d;
    }

    public void solveTarget() {

        final double rhobeg = 0.5;
        final double rhoend = 1.0e-6;
        final int iprint = 0;
        final int maxfun = 3500;

        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {

            int xi = 0;

            // update joints
            for (int i = 0; i < getLeftArm().size(); i++) {
                getLeftArm().getJoint(i).setAngle(x[xi++]);
            }

            for (int i = 0; i < getRightArm().size(); i++) {
                getRightArm().getJoint(i).setAngle(x[xi++]);
            }

            getLeftArm().setOffset(new Vector3d(x[xi++], x[xi++], 0));
            getRightArm().setOffset(new Vector3d(x[xi++], x[xi++], 0));

            updatePositions();
            int ci = 0;
            for (int i = 0; i < getLeftArm().size(); i++) {
                
                Joint j = getLeftArm().getJoint(i);
                c[ci++]=j.getMaxAngle1()-x[i];
                c[ci++]=x[i]-j.getMinAngle1();
            }

            double sum = 0;

            // fixed distance of shoulders
            double sdc = c[ci++] = shouldersDistanceGoal - getShouldersDistance();
            c[ci++] = -sdc;

            //solveDistanceConstraint(getLeftShoulderPosition(), getRightShoulderPosition(), getShouldersDistanceGoal());
            // elbows coplanarity
            Vector3d leftElbowTarget = new Vector3d(-20, 0, 0);
            Vector3d rightElbowTarget = new Vector3d(20, 0, 0);

            //Intersectiond.distancePointPlane(...)
            double d = distancePointAndPlane(getLeftShoulderPosition(), leftElbowTarget, getLeftWristPosition(), getLeftElbowPosition());
            c[ci++] = d;
            c[ci++] = -d;

            d = distancePointAndPlane(getRightShoulderPosition(), rightElbowTarget, getRightWristPosition(), getRightElbowPosition());
            c[ci++] = d;
            c[ci++] = -d;

            // elbows targets
            Vector3d sw = getLeftWristPosition().sub(getLeftShoulderPosition(), new Vector3d());
            Vector3d se = getLeftElbowPosition().sub(getLeftShoulderPosition(), new Vector3d());
            Vector3d set = leftElbowTarget.sub(getLeftShoulderPosition(), new Vector3d());
            Vector3d seCsw = se.cross(sw, new Vector3d());
            Vector3d setCsw = set.cross(sw, new Vector3d());
            d = seCsw.dot(setCsw);
            c[ci++] = d;

            sw = getRightWristPosition().sub(getRightShoulderPosition(), new Vector3d());
            se = getRightElbowPosition().sub(getRightShoulderPosition(), new Vector3d());
            set = rightElbowTarget.sub(getRightShoulderPosition(), new Vector3d());
            seCsw = se.cross(sw, new Vector3d());
            setCsw = set.cross(sw, new Vector3d());
            d = seCsw.dot(setCsw);
            c[ci++] = d;

            // root
            d = c[ci++] = getLeftArm().getOffset().distance(getRightArm().getOffset());
            c[ci++] = -d;

            Vector3d v = getLeftArm().getOffset().add(getRightArm().getOffset(), new Vector3d()).mul(.5d);
            //c[ci++] = v.y;
            //c[ci++] = -v.y;

            // objective function (minimize the distance from targets)
            sum += getLeftWristPosition().distanceSquared(getLeftArm().getTarget());
            sum += getRightWristPosition().distanceSquared(getRightArm().getTarget());

            return sum;
        };

        ArrayList<Double> X = new ArrayList<>();

        // arms
        for (Joint j : getLeftArm().getJoints()) {
            X.add(j.getAngle());
        }

        for (Joint j : getRightArm().getJoints()) {
            X.add(j.getAngle());
        }

        // root
        X.add(getLeftArm().getOffset().x);
        X.add(getLeftArm().getOffset().y);
        X.add(getRightArm().getOffset().x);
        X.add(getRightArm().getOffset().y);
        double x[] = new double[X.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = X.get(i);
        }

        CobylaExitStatus result = null;
        int nc = X.size() * 2+2+4;
        //int nc = 8 + 2 + 4;
        for (int i = 0; i < iterations; i++) {
            result = Cobyla.FindMinimum(calcfc, x.length, nc, x, rhobeg, rhoend, iprint, maxfun);
        }

        //updatePositions();
        //System.out.println(result.toString());
        //Log.printArray(x);
    }

}
