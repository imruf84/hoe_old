package prototype;

import physics.Vector3D;

public class CurvePoint extends Vector3D {
    double t;

    public CurvePoint(CurvePoint cp) {
        set(cp);
        t=cp.getT();
    }

    public CurvePoint(Vector3D v, double t) {
        super(v);
        this.t = t;
    }

    public double getT() {
        return t;
    }

}
