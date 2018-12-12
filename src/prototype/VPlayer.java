package prototype;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import physics.Vector3D;

public class VPlayer extends Player {

    public static final double SHAPE_SIZE = 10d;

    private final Group container = new Group();
    private final Group pane = new Group();
    private final Group pathGroup = new Group();
    private final Group pathPointsGroup = new Group();
    private final NodeGestures NODE_GESTURES;

    private Shape shape;
    private Label label;

    public VPlayer(String name, Vector3D position, NodeGestures nodeGestures) {
        super(name, position);

        this.NODE_GESTURES = nodeGestures;

        init(nodeGestures);
    }

    public Group getContainer() {
        return container;
    }

    public Group getPane() {
        return pane;
    }

    private void init(NodeGestures nodeGestures) {

        getPane().addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        getPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        getPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            if (getPath().getPoints().size() > 0) {
                Node node = (Node) event.getSource();
                Vector3D lp = getPath().getPoints().get(0);
                lp.set(node.getTranslateX(), node.getTranslateY(), 0);
                updatePath();
            }
        });

        shape = new Circle(SHAPE_SIZE);
        shape.setFill(new Color(1, 0, 0, .5));
        shape.setStrokeWidth(1d);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.setStroke(Color.BLACK);

        label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setLineSpacing(-SHAPE_SIZE / 10d);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font(SHAPE_SIZE / 3));

        getPane().getChildren().addAll(shape, label);
        getContainer().getChildren().addAll(pathGroup, pathPointsGroup, getPane());

        update();
    }

    public void updatePath() {

        getPathGroup().getChildren().clear();

        boolean b = true;
        for (CurvePoint cn : getPath().generatePathPointsByLength(1d)) {
            if (!b) {
                Circle c = new Circle(cn.x, cn.y, .5d, Color.BLACK);
                getPathGroup().getChildren().add(c);
                c.toBack();
            }
            b = !b;
        }
    }

    public void update() {

        getContainer().setTranslateX(getPosition().x);
        getContainer().setTranslateY(getPosition().y);

        label.setText(getName() + "\na\nb");
        Platform.runLater(() -> {
            label.setTranslateX(-label.getWidth() / 2d);
            label.setTranslateY(-label.getHeight() / 2d);
        });

    }

    private Group getPathGroup() {
        return pathGroup;
    }

    @Override
    public void addNavigationPoint(Vector3D p) {
        
        super.addNavigationPoint(p);

        Circle c = new Circle(4);
        c.setUserData(p);
        c.setFill(new Color(0, 0, 1, .5));
        c.setTranslateX(p.x);
        c.setTranslateY(p.y);
        pathPointsGroup.getChildren().add(c);
        c.toFront();

        c.addEventFilter(MouseEvent.MOUSE_PRESSED, NODE_GESTURES.getOnMousePressedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, NODE_GESTURES.getOnMouseDraggedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            Node node = (Node) event.getSource();
            Vector3D lp = (Vector3D) node.getUserData();
            lp.set(node.getTranslateX(), node.getTranslateY(), 0);
            updatePath();
        });

        updatePath();

    }

}
