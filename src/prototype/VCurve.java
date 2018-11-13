package prototype;

import java.util.LinkedList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import physics.Vector3D;

public class VCurve extends Group {

    private final Curve curve;

    private final Group path = new Group();
    private final Group hull = new Group();
    private final Group points = new Group();

    private final NodeGestures nodeGestures;

    VCurve(int iterations, boolean closed, NodeGestures nodeGestures) {
        super();

        curve = new Curve(iterations, closed);

        this.nodeGestures = nodeGestures;

        init();
    }

    private void init() {
        getChildren().addAll(hull, path, points);
    }

    public Curve getCurve() {
        return curve;
    }

    public Group getPath() {
        return path;
    }

    public Group getHull() {
        return hull;
    }

    public Group getPoints() {
        return points;
    }
    
    private Color colors[] = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PLUM, Color.GRAY};

    public void addPoint(Vector3D p) {

        getCurve().addPoint(p);

        Circle c = new Circle(2d);
        c.setUserData(p);
        c.setTranslateX(p.x);
        c.setTranslateY(p.y);
        c.setTranslateZ(p.z);
        //c.setFill(Color.BLUE);
        c.setFill(colors[(getCurve().getPoints().size()-1)%colors.length]);
        c.setStroke(Color.BLACK);
        points.getChildren().add(c);

        c.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {

            for (Node n : getPoints().getChildren()) {
                Vector3D lp = (Vector3D) n.getUserData();
                lp.set(n.getTranslateX(), n.getTranslateY(), n.getTranslateZ());
            }

            updateVisuals();

            event.consume();
        });

    }

    public void updateVisuals() {

        getCurve().updateControlPoints();

        int from = 0;
        int to = getCurve().getPaths().size();
        if (!getCurve().isClosed()) {
            from++;
            to--;
        }
        
        getHull().getChildren().clear();
        //for (int j = 0; j < getCurve().getPaths().size(); j++) {
        //for (int j = 1; j < getCurve().getPaths().size()-1; j++) {
        for (int j = from; j < to; j++) {
            CurvePath cp = getCurve().getPaths().get(j);
            for (int i = 1; i < cp.getDegree(); i++) {
                Line line = new Line(cp.getControlPoint(i - 1).x, cp.getControlPoint(i - 1).y, cp.getControlPoint(i).x, cp.getControlPoint(i).y);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.getStrokeDashArray().addAll(4d, 4d);
                getHull().getChildren().add(line);
            }
        }

        getPath().getChildren().clear();
        //for (int j = 0; j < getCurve().getPaths().size(); j++) {
        //for (int j = 1; j < getCurve().getPaths().size()-1; j++) {
        for (int j = from; j < to; j++) {
            CurvePath cp = getCurve().getPaths().get(j);
            LinkedList<Vector3D> pl = cp.calculatePath();
            for (int i = 1; i < pl.size(); i++) {
                Vector3D prev = pl.get(i - 1);
                Vector3D curr = pl.get(i);
                Line line = new Line(prev.x, prev.y, curr.x, curr.y);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setStroke(Color.RED);
                getPath().getChildren().add(line);
            }
        }

    }

}
