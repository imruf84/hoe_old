package hoe.curves;

import hoe.physics.Vector3D;

public class CurvePoint extends Vector3D {

    public double t;

    public CurvePoint(CurvePoint cp) {
        set(cp);
    }

    public final void set(CurvePoint cp) {
        set(cp.x, cp.y, cp.z);
        t = cp.t;
    }

    public CurvePoint(Vector3D v, double t) {
        super(v);
        this.t = t;
    }

    public double getT() {
        return t;
    }

    @Override
    public String toString() {
        return "CurvePoint{" + "x=" + x + ", y=" + y + ", z=" + z + ", " + "t=" + t + '}';
    }

}
