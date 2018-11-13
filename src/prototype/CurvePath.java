package prototype;

import java.util.LinkedList;
import physics.Vector3D;

public class CurvePath {

    private final Vector3D c[] = new Vector3D[getDegree()];

    public CurvePath(Vector3D c0, Vector3D c1, Vector3D c2) {

        int n = getDegree();
        for (int i = 0; i < n; i++) {
            c[i] = new Vector3D();
        }

        c[0].set(c0);
        c[1].set(c1);
        c[2].set(c2);
    }

    public CurvePath() {
        this(new Vector3D(), new Vector3D(), new Vector3D());
    }

    public final int getDegree() {
        return 3;
    }

    public Vector3D eval(double t) {

        Vector3D result = new Vector3D();

        result.add(Vector3D.scale(c[0], (1 - t) * (1 - t)));
        result.add(Vector3D.scale(c[1], 2 * (1 - t) * t));
        result.add(Vector3D.scale(c[2], t * t));

        return result;
    }

    public LinkedList<Vector3D> calculatePath() {

        LinkedList<Vector3D> result = new LinkedList<>();

        double dt = .01d;
        double t = 0;
        while (t < 1 + dt) {

            Vector3D p = eval(Math.min(t, 1));
            result.add(p);

            t += dt;
        }

        return result;
    }

    public Vector3D getControlPoint(int i) {
        return c[i];
    }
}
