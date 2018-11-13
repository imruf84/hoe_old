package prototype;

import Jama.Matrix;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import physics.Vector3D;

public class Curve {

    private final LinkedList<CurvePath> paths = new LinkedList<>();
    private final LinkedList<Vector3D> points = new LinkedList<>();
    private final int iterations;
    private boolean closed = true;

    public Curve(int iterations, boolean closed) {
        this.iterations = iterations;
        this.closed = closed;
    }

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

    public int getIterations() {
        return iterations;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
        updateControlPoints();
    }

    public static double area(Vector3D va, Vector3D vb, Vector3D vc) {

        double a = Vector3D.subtract(va, vb).length();
        double b = Vector3D.subtract(va, vc).length();
        double c = Vector3D.subtract(vb, vc).length();

        double s = (a + b + c) / 2.0d;
        double x = (s * (s - a) * (s - b) * (s - c));

        if (x > 0) {
            return Math.sqrt(x);
        }

        return Math.pow(10, -10);
    }

    public int getIndex(int i, int n) {

        if (isClosed()) {
            return (i < 0 ? i + n : i) % n;
        }

        return Math.min(Math.max(i, 0), n - 1);

    }

    public final void updateControlPoints() {

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
            
//l[0]=.5;
//System.out.println(Arrays.toString(l));
            // c_i,0, c_i,2

            for (int i = 0; i < n; i++) {
//            for (int i = 1; i < n; i++) {
                c(i, 2).set(Vector3D.add(Vector3D.scale(c(i, 1), 1 - l[i]), Vector3D.scale(c(i + 1, 1), l[i])));
                c(i + 1, 0).set(c(i, 2));
            }

            if (!isClosed()) {
//c(0,2).set(p(0));

                c(1, 0).set(p(0));
                c(n - 2, 2).set(p(n - 1));
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
                    t[i] = cubic.getRootForCurve();
                }
            }
t[0]=1;
//System.out.println(Arrays.toString(t));
            
            // c_i,1
            int coords = 2;
            int m = n * coords;
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

  /*          
            if (!isClosed()) {
                Matrix AA = new Matrix(A.getArray());
                A = new Matrix(m - 2 * coords, m - 2 * coords);
                Matrix bb = new Matrix(b.getArray());
                b = new Matrix(m - 2 * coords, 1);

                for (int i = 0; i < A.getRowDimension(); i++) {
                    for (int j = 0; j < A.getColumnDimension(); j++) {
                        A.set(i, j, AA.get(i + coords, j + coords));
                    }
                    b.set(i, 0, bb.get(i + coords, 0));
                }
            }
*/
            Matrix x = A.solve(b);
            for (int i = 0; i < n; i++) {
//            for (int i = 0; i < n-2; i++) {
                Vector3D p = c(i, 1);
//                Vector3D p = c(i+1, 1);
                p.set(x.get(i * coords + 0, 0), x.get(i * coords + 1, 0), 0);
            }

            Matrix Residual = A.times(x).minus(b);
            double rnorm = Residual.normInf();
        }
  
/*        
        for (int i = 0; i < getPoints().size(); i++) {
            
//            if (i>1 && i<getPoints().size()-2) continue;
            
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.print(i+"\t"+df.format(p(i).x)+","+df.format(p(i).y)+"\t\t");
            System.out.print(df.format(c(i,0).x)+","+df.format(c(i,0).y)+"\t\t");
            System.out.print(df.format(c(i,1).x)+","+df.format(c(i,1).y)+"\t\t");
            System.out.println(df.format(c(i,2).x)+","+df.format(c(i,2).y));
        }
        System.out.println("-----------------------------------");*/
    }

}
