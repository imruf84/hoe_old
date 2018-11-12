package prototype.curves;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
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

    private int getDegree() {
        return 3;
    }

    public Vector3D eval(double t) {

        Vector3D result = new Vector3D();

        result.add(Vector3D.scale(c[0], (1 - t) * (1 - t)));
        result.add(Vector3D.scale(c[1], 2 * (1 - t) * t));
        result.add(Vector3D.scale(c[2], t * t));

        return result;
    }

    public Group getSegmens() {

        Group result = new Group();

        Vector3D prev = null;
        double dt = .05d;
        double t = 0;
        while (t < 1 + dt) {

            Vector3D curr = eval(Math.min(t, 1));

            if (prev != null) {
                Line line = new Line(prev.x, prev.y, curr.x, curr.y);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setStroke(Color.RED);
                result.getChildren().add(line);
            }

            prev = curr;

            t += dt;
        }

        for (int i = 1; i < getDegree(); i++) {
            Line line = new Line(c[i - 1].x, c[i - 1].y, c[i].x, c[i].y);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.getStrokeDashArray().addAll(4d, 4d);
            result.getChildren().add(line);
        }
        
        for (Vector3D p : c) {
            Circle c = new Circle(1d);
            c.setTranslateX(p.x);
            c.setTranslateY(p.y);
            c.setTranslateZ(p.z);
            c.setFill(Color.GREEN);
            result.getChildren().add(c);
        }

        return result;
    }

    public Vector3D getControlPoint(int i) {
        return c[i];
    }
}
