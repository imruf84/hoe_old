package hoe.skeleton;

import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Joint {

    private double angle;
    private double length;
    private Joint next = null;
    private Joint prev = null;
    private final Vector3d tail = new Vector3d();

    public Joint(double length, double angle) {
        this(length, angle, null);
    }

    public Joint(double length, double angle, Joint parent) {
        this.length = length;
        this.angle = angle;

        setParent(parent);
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

    public void setLength(double length) {
        this.length = length;
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
