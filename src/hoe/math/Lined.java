package hoe.math;

import org.joml.Intersectiond;
import org.joml.Vector3d;

public class Lined {
    
    private final Vector3d p0;
    private final Vector3d p1;

    public Lined(Vector3d p0, Vector3d p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    public Vector3d getP0() {
        return p0;
    }

    public Vector3d getP1() {
        return p1;
    }
    
    public double distance(Vector3d p) {
        return Intersectiond.distancePointLine(p.x, p.y, p.z, p0.x, p0.y, p0.z, p1.x, p1.y, p1.z);
    }
}
