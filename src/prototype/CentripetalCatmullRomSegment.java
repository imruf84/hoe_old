package prototype;

import physics.Vector3D;

public class CentripetalCatmullRomSegment {

    private double[] time;
    private Vector3D[] points;

    protected CentripetalCatmullRomSegment(Vector3D[] points) {
        if (points.length != 4) {
            throw new RuntimeException(String.format("Need exactly 4 points to initialize CentripetalCatmullRom, %d provided.", points.length));
        }

        this.points = points;
        time = new double[4];
        time[0] = 0;
        for (int i = 1; i < 4; i++) {
            double len = 0;
            if (i > 0) {
                len = points[i].cpy().sub(points[i - 1]).len();
            }
            if (len <= 0) {
                len += 0.0001d;
            }
            time[i] = (float) Math.sqrt(len) + time[i - 1];
        }
    }

    public Vector3D pointAt(double t) {
        t = t * (time[2] - time[1]) + time[1];

        Vector3D A1 = points[0].cpy().scale((time[1] - t) / (time[1] - time[0]))
                .add(points[1].cpy().scale((t - time[0]) / (time[1] - time[0])));
        Vector3D A2 = points[1].cpy().scale((time[2] - t) / (time[2] - time[1]))
                .add(points[2].cpy().scale((t - time[1]) / (time[2] - time[1])));
        Vector3D A3 = points[2].cpy().scale((time[3] - t) / (time[3] - time[2]))
                .add(points[3].cpy().scale((t - time[2]) / (time[3] - time[2])));

        Vector3D B1 = A1.cpy().scale((time[2] - t) / (time[2] - time[0]))
                .add(A2.cpy().scale((t - time[0]) / (time[2] - time[0])));
        Vector3D B2 = A2.cpy().scale((time[3] - t) / (time[3] - time[1]))
                .add(A3.cpy().scale((t - time[1]) / (time[3] - time[1])));

        Vector3D C = B1.cpy().scale((time[2] - t) / (time[2] - time[1]))
                .add(B2.cpy().scale((t - time[1]) / (time[2] - time[1])));

        return C;
    }
}
