package prototype;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import prototype.curves.CurvePath;

public class VCurve extends StackPane {

    private final Curve curve = new Curve();
    private final Group path = new Group();
    public static final double SHAPE_SIZE = 100d;

    VCurve(NodeGestures nodeGestures) {
        super();

        init(nodeGestures);
    }

    private void init(NodeGestures nodeGestures) {

        addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        getChildren().add(path);

        update();
    }

    public Curve getCurve() {
        return curve;
    }

    public Group getPath() {
        return path;
    }

    public void update() {

        getCurve().updateControlPoints(30);
        getPath().getChildren().clear();

        for (CurvePath cp : getCurve().getPaths()) {
            getPath().getChildren().add(cp.getSegmens());
        }
    }

}
