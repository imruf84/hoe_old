package prototype;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class NodeGestures {

    private DragContext nodeDragContext = new DragContext();
    private PannableCanvas canvas;

    public NodeGestures(PannableCanvas canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

            if (!event.isPrimaryButtonDown()) {
                return;
            }

            nodeDragContext.mouseAnchorX = event.getSceneX();
            nodeDragContext.mouseAnchorY = event.getSceneY();

            javafx.scene.Node node = (javafx.scene.Node) event.getSource();
            node.toFront();

            nodeDragContext.translateAnchorX = node.getTranslateX();
            nodeDragContext.translateAnchorY = node.getTranslateY();

        }

    };

    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if (!event.isPrimaryButtonDown()) {
                return;
            }

            double scale = canvas.getScale();

            javafx.scene.Node node = (javafx.scene.Node) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

            event.consume();

        }
    };
}
