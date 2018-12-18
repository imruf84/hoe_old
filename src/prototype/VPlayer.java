package prototype;

public class VPlayer{
    
}

/*
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import physics.Vector3D;

public class VPlayer extends Player {

    public static final double SHAPE_SIZE = 10d;

    private final Group container = new Group();
    private final Group pane = new Group();
    public final Group pathGroup = new Group();
    public final Group pathPointsGroup = new Group();
    private final ArrayList<Node> pathPoints = new ArrayList<>();
    private final NodeGestures NODE_GESTURES;

    private Shape shape;
    private Label label;

    public VPlayer(String name, Vector3D position, double radius, double step, NodeGestures nodeGestures) {
        super(name, position, radius, step);

        this.NODE_GESTURES = nodeGestures;

        init(nodeGestures);
    }
    
    public static Color getRandomColor() {
        Random rnd = new Random();
        return new Color(rnd.nextFloat(),rnd.nextFloat(),rnd.nextFloat(),1f);
    }

    public Group getContainer() {
        return container;
    }

    public Group getPane() {
        return pane;
    }
    
    @Override
    protected  void recalculatePath(Vector3D newPos) {
        super.recalculatePath(newPos);
        updatePath();
    }
    
    @Override
    public void removeNavigationPoint(int i) {
        
        Node n = pathPoints.remove(i);
        pathPointsGroup.getChildren().remove(n);
        
        super.removeNavigationPoint(i);
    }
    
    @Override
    public void doOneStep(CurvePoint nextPos) {
        super.doOneStep(nextPos);
    
        // Hide control points behind.
        int n = (int) Math.floor(getPosition().t);
        for (int i = 0; i < n; i++) {
            pathPoints.get(i).setVisible(false);
        }
    }
    
    public void setStrokeColor(Color c) {
        shape.setStroke(c);
    }
    
    public void setStrokeColor() {
        shape.setStroke(Color.BLACK);
    }

    private void init(NodeGestures nodeGestures) {

        getPane().addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        getPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        getPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            if (getPath().getPoints().size() > 0) {
                Node node = (Node) event.getSource();
                Vector3D newPos = new Vector3D(node.getTranslateX(), node.getTranslateY(), 0);
                getPosition().set(newPos);
                recalculatePath(newPos);
            }
        });

        shape = new Circle(getRadius());
        shape.setFill(new Color(1, 0, 0, .5));
        shape.setStrokeWidth(1d);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.setStroke(Color.BLACK);

        label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setLineSpacing(-SHAPE_SIZE / 10d);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font(SHAPE_SIZE / 3));
        
        double crossSize = getRadius();
        Line l1 = new Line(-crossSize,0,crossSize,0);
        l1.setStrokeWidth(.3d);
        Line l2 = new Line(0,-crossSize,0,crossSize);
        l2.setStrokeWidth(.3d);

        Circle shape2 = new Circle(getRadius()+getMaxStep());
        shape2.setFill(null);
        shape2.setStrokeWidth(.2d);
        shape2.setStrokeLineCap(StrokeLineCap.ROUND);
        shape2.setStroke(Color.BLACK);
        
        getPane().getChildren().addAll(l1,l2,shape, shape2, label);
        //getPane().setMouseTransparent(true);
        getContainer().getChildren().addAll(pathGroup, getPane(), pathPointsGroup);
        pathPointsGroup.toFront();

        update();
    }

    public void updatePath() {

        getPathGroup().getChildren().clear();

        boolean b = false;
        //for (CurvePoint cn : getPath().generatePathPointsByLength(1d)) {
        for (CurvePoint cn : getPath().generatePathPointsByLength(getMaxStep()/2d)) {
            if (!b) {
                Circle c = new Circle(cn.x, cn.y, .5d, Color.BLACK);
                getPathGroup().getChildren().add(c);
                c.toBack();
            }
            b = !b;
        }
    }

    @Override
    public void update() {
        
        super.update();

        //getContainer().setTranslateX(getPosition().x);
        //getContainer().setTranslateY(getPosition().y);
        getPane().setTranslateX(getPosition().x);
        getPane().setTranslateY(getPosition().y);

        //label.setText(getName() + "\na\nb");
        label.setText(getName());
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
        pathPoints.add(c);
        c.toFront();

        c.addEventFilter(MouseEvent.MOUSE_PRESSED, NODE_GESTURES.getOnMousePressedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, NODE_GESTURES.getOnMouseDraggedEventHandler());
        c.addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            Node node = (Node) event.getSource();
            Vector3D lp = (Vector3D) node.getUserData();
            lp.set(node.getTranslateX(), node.getTranslateY(), 0);
            recalculatePath(getPosition());
            updatePath();
        });

        updatePath();

    }

}
*/