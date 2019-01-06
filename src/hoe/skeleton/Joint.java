package hoe.skeleton;

import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Joint {

    private double angle;
    private double length;
    private Joint next = null;
    private Joint prev = null;
    private double minAngle = -360;
    private double maxAngle = 360;
    private final Vector3d tail = new Vector3d();


    public Joint(double length, double angle, double minAngle, double maxAngle) {
        this(length, angle);
        setAngleLimits(minAngle, maxAngle);
    }
    
    private void setAngleLimits(double min, double max) {
        setMinAngle(Math.min(min, max));
        setMaxAngle(Math.max(min, max));
    }
    
    public Joint(double length, double angle) {
        this.length = length;
        this.angle = angle;
    }

    public double getMinAngle() {
        return minAngle;
    }

    public final void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public final void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    private void setParent(Joint parent) {
        if (parent == null) {
            return;
        }

        this.prev = parent;
        parent.next = this;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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