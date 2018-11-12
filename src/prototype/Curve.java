package prototype;

import Jama.Matrix;
import java.util.LinkedList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import physics.Vector3D;
import prototype.curves.CurvePath;

public class Curve {

    private final LinkedList<CurvePath> paths = new LinkedList<>();
    private final LinkedList<Vector3D> points = new LinkedList<>();

    public void addPoint(Vector3D p) {
        getPoints().add(p);
        getPaths().add(new CurvePath(p, p, p));
    }

    public Vector3D p(int i) {
        return getPoints().get(i);
    }

    public Vector3D c(int i, int j) {
        int n = getPoints().size();
        return getPaths().get(getIndex(i, n)).getControlPoint(j);
    }

    public LinkedList<CurvePath> getPaths() {
        return paths;
    }

    public LinkedList<Vector3D> getPoints() {
        return points;
    }

    public Group getCurve() {

        Group result = new Group();

        for (CurvePath cp : getPaths()) {
            result.getChildren().add(cp.getSegmens());
        }

        for (Vector3D p : getPoints()) {
            Circle c = new Circle(2d);
            c.setTranslateX(p.x);
            c.setTranslateY(p.y);
            c.setTranslateZ(p.z);
            c.setFill(Color.BLUE);
            c.setStroke(Color.BLACK);
            result.getChildren().add(c);
        }

        return result;
    }

    public static double area(Vector3D va, Vector3D vb, Vector3D vc) {

        double a = Vector3D.subtract(va,vb).length();
        double b = Vector3D.subtract(va,vc).length();
        double c = Vector3D.subtract(vb,vc).length();

        double s = (a + b + c) / 2.0d;
        double x = (s * (s - a) * (s - b) * (s - c));

        return Math.sqrt(x);
    }

    public int getIndex(int i, int n) {
        return (i < 0 ? i + n : i) % n;
    }

    public final void updateControlPoints(int iterations) {

        if (getPoints().size() < 2) {
            return;
        }

        boolean init = true;
        int n = getPoints().size();
        
        for (int k = 0; k < iterations; k++) {
            
            double l[] = new double[n];
            
            // Lambda
            if (init) {
                for (int i = 0; i < n; i++) {
                    l[i] = .5d;
                    c(i, 1).set(p(i));
                }
                init = false;
            } else {
                for (int i = 0; i < n; i++) {
                    double a = Math.sqrt(Math.abs(area(c(i, 0), c(i, 1), c(i + 1, 1))));
                    double b = Math.sqrt(Math.abs(area(c(i, 1), c(i + 1, 1), c(i + 1, 2))));
                    l[i] = a / (a + b);
                }
            }

            // c_i,0, c_i,2
            for (int i = 0; i < n; i++) {
                c(i, 2).set(Vector3D.add(Vector3D.scale(c(i, 1), 1 - l[i]), Vector3D.scale(c(i + 1, 1), l[i])));
                c(i + 1, 0).set(c(i, 2));
            }

            if (getPoints().size() < 3) {
                return;
            }

            // t_i
            double t[] = new double[n];
            for (int i = 0; i < n; i++) {
                double a = Vector3D.subtract(c(i, 2), c(i, 0)).lengthSq();
                double b = 3 * Vector3D.subtract(c(i, 2), c(i, 0)).dot(Vector3D.subtract(c(i, 0), p(i)));
                double c = Vector3D.subtract(Vector3D.subtract(Vector3D.scale(c(i, 0), 3), Vector3D.scale(p(i), 2)), c(i, 2)).dot(Vector3D.subtract(c(i, 0), p(i)));
                double d = -Vector3D.subtract(c(i, 0), p(i)).lengthSq();
                if (a != 0) {
                    Cubic cubic = new Cubic();
                    cubic.solve(a, b, c, d);
                    t[i] = cubic.x1;
                }
            }

            // c_i,1
            int coords = 2;
            int m = getPoints().size() * coords;
            Matrix A = new Matrix(m, m);
            Matrix b = new Matrix(m, 1);
            for (int i = 0; i < n; i++) {

                int indexM1 = getIndex(i - 1, n);
                int indexP1 = getIndex(i + 1, n);

                double lim1 = l[indexM1];
                double li = l[i];
                double ti = t[i];

                double ca = (1 - lim1) * (1 - ti) * (1 - ti);
                double cb = li * ti * ti;
                double cc = lim1 * (1 - ti) * (1 - ti) + (2 - (1 + li) * ti) * ti;
                A.set(i * coords + 0, indexM1 * coords + 0, ca);
                A.set(i * coords + 0, indexP1 * coords + 0, cb);
                A.set(i * coords + 0, i * coords + 0, cc);

                A.set(i * coords + 1, indexM1 * coords + 1, ca);
                A.set(i * coords + 1, indexP1 * coords + 1, cb);
                A.set(i * coords + 1, i * coords + 1, cc);

                Vector3D p = getPoints().get(i);
                b.set(i * coords + 0, 0, p.x);
                b.set(i * coords + 1, 0, p.y);
            }

            Matrix x = A.solve(b);
            for (int i = 0; i < n; i++) {
                Vector3D p = c(i, 1);
                p.set(x.get(i * coords + 0, 0), x.get(i * coords + 1, 0), 0);
            }

            Matrix Residual = A.times(x).minus(b);
            double rnorm = Residual.normInf();
        }
    }

}
