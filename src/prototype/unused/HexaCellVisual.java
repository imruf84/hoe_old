package prototype.unused;

import java.util.Arrays;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import prototype.NodeGestures;

public class HexaCellVisual extends StackPane {
    
    private final HexaCell hexaCell;
    private Polygon hexagon;
    private Label label;

    
    HexaCellVisual(HexaCell hexaCell, NodeGestures nodeGestures) {
        super();

        this.hexaCell = hexaCell;

        init(nodeGestures);
    }

    private void init(NodeGestures nodeGestures) {
//        addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
//        addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        hexagon = new Polygon(new Hexagon(getHexaCell().getSide()).getPoints());
        hexagon.setFill(Color.RED);
        hexagon.setStroke(Color.BLACK);

        label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.BLACK);
        label.setFont(new Font(getHexaCell().getSide() / 1.7));
        
        getChildren().addAll(hexagon, label);

        update();
    }

    public HexaCell getHexaCell() {
        return hexaCell;
    }

    public void update() {
        
        setTranslateX(getHexaCell().getPosition()[0]);
        setTranslateY(getHexaCell().getPosition()[1]);
        
        label.setText(Arrays.toString(getHexaCell().getCoords()));
    }

}
