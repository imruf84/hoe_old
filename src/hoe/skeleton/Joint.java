package hoe.skeleton;

import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Joint {

    private double angle1;
    private double length;
    private Joint next = null;
    private Joint prev = null;
    private double minAngle1 = -360;
    private double maxAngle1 = 360;
    private final Vector3d tail = new Vector3d();


    public Joint(double length, double angle1, double minAngle1, double maxAngle1) {
        this(length, angle1);
        setAngle1Limits(minAngle1, maxAngle1);
    }
    
    private void setAngle1Limits(double min, double max) {
        setMinAngle1(Math.min(min, max));
        setMaxAngle1(Math.max(min, max));
    }
    
    public Joint(double length, double angle) {
        this.length = length;
        this.angle1 = angle;
    }

    public double getMinAngle1() {
        return minAngle1;
    }

    public final void setMinAngle1(double minAngle) {
        this.minAngle1 = minAngle;
    }

    public double getMaxAngle1() {
        return maxAngle1;
    }

    public final void setMaxAngle1(double maxAngle) {
        this.maxAngle1 = maxAngle;
    }

    private void setParent(Joint parent) {
        if (parent == null) {
            return;
        }

        this.prev = parent;
        parent.next = this;
    }

    public double getAngle() {
        return angle1;
    }

    public void setAngle(double angle) {
        this.angle1 = angle;
    }

    public double getLength() {
        return length;
    }

    public Joint getNext() {
        return next;
    }

    public void setNext(Joint next) {
        this.next = next;
    }

    public Joint getPrev() {
        return prev;
    }

    public void setPrev(Joint prev) {
        this.prev = prev;
    }

    public Vector3d getTail() {
        return tail;
    }

    public Vector3d getHead() {

        if (getPrev() != null) {
            getPrev().getTail();
        }

        return null;
    }

    public void updatePosition(double angle, Vector3d offset) {

        double lAngle = angle + getAngle();

        new Matrix4d()
                .translate(offset)
                .rotate(Math.toRadians(lAngle), new Vector3d(0, 0, 1).normalize())
                .translate(getLength(), 0, 0)
                .transformPosition(new Vector3d(), getTail());

        if (getNext() != null) {
            getNext().updatePosition(lAngle, getTail());
        }
    }

}
