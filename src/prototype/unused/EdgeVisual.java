package prototype.unused;

import java.util.Objects;
import javafx.scene.shape.Line;

public class EdgeVisual extends Line {
/*
    private final NodeVisual node1;
    private final NodeVisual node2;

    public EdgeVisual(NodeVisual node1, NodeVisual node2) {
        super();

        this.node1 = node1;
        this.node2 = node2;

        startXProperty().bind(node1.translateXProperty().add(NodeVisual.R*Hexagon.HEX_UNIT_X_DOUBLE/2d));
        startYProperty().bind(node1.translateYProperty().add(NodeVisual.R));
        endXProperty().bind(node2.translateXProperty().add(NodeVisual.R*Hexagon.HEX_UNIT_X_DOUBLE/2d));
        endYProperty().bind(node2.translateYProperty().add(NodeVisual.R));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1.getNode().getID(), node2.getNode().getID());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EdgeVisual other = (EdgeVisual) obj;

        return (Objects.equals(this.node1, other.node1) && Objects.equals(this.node2, other.node2)) || (Objects.equals(this.node1, other.node2) && Objects.equals(this.node2, other.node1));
    }
*/
}
