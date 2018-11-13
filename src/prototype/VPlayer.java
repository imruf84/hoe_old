package prototype;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class VPlayer extends StackPane {

    public static final double SHAPE_SIZE = 100d;

    private final Player player;
    private Shape shape;
    private Label label;
    private final Point2D position = new Point2D(0, 0);

    VPlayer(Player player, NodeGestures nodeGestures) {
        super();

        this.player = player;

        init(nodeGestures);
    }

    private void init(NodeGestures nodeGestures) {
        addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        shape = new Circle(SHAPE_SIZE);
        shape.setFill(Color.RED);
        shape.setStrokeWidth(6d);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.getStrokeDashArray().addAll(10d, 15d);
        shape.setStroke(Color.BLACK);

        label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setLineSpacing(-SHAPE_SIZE / 10d);
        label.setTextFill(Color.BLACK);
        label.setFont(new Font(SHAPE_SIZE / 3.5));

        getChildren().addAll(shape, label);

        update();
    }

    public void update() {

        setTranslateX(getPosition().getX());
        setTranslateY(getPosition().getY());

        label.setText(getPlayer().getName() + "\na\nb");
    }

    public Player getPlayer() {
        return player;
    }

    public Point2D getPosition() {
        return position;
    }

}
