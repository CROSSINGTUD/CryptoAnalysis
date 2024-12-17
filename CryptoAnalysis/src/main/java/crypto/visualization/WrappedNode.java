package crypto.visualization;

import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AbstractOrderError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.PredicateConstraintError;
import java.util.Objects;
import org.graphper.api.Node;
import org.graphper.api.attributes.Color;
import org.graphper.api.attributes.NodeShapeEnum;

public class WrappedNode {

    private final AbstractError error;
    private Node graphicalNode;

    public WrappedNode(AbstractError error) {
        this.error = error;
    }

    public Node asGraphicalNode() {
        if (graphicalNode == null) {
            graphicalNode =
                    Node.builder()
                            .label(getLabel())
                            .shape(NodeShapeEnum.RECT)
                            .fillColor(getColor())
                            .build();
        }

        return graphicalNode;
    }

    private String getLabel() {
        return error.getClass().getSimpleName()
                + "\n"
                + error.getErrorStatement()
                + "\nLine: "
                + error.getLineNumber();
    }

    private Color getColor() {
        if (error instanceof ForbiddenMethodError) {
            return Color.BLUE;
        } else if (error instanceof AbstractOrderError) {
            return Color.ORANGE;
        } else if (error instanceof AbstractConstraintsError) {
            return Color.YELLOW;
        } else if (error instanceof PredicateConstraintError) {
            return Color.INDIGO;
        } else {
            return Color.WHITE;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedNode other && Objects.equals(error, other.error);
    }

    @Override
    public String toString() {
        return "Node: " + error;
    }
}
