package crypto.visualization;

import java.util.Objects;
import org.graphper.api.Line;

public class WrappedLine {

    private final WrappedNode from;
    private final WrappedNode to;
    private Line line;

    private WrappedLine(WrappedNode from, WrappedNode to) {
        this.from = from;
        this.to = to;
    }

    public static WrappedLine forLine(WrappedNode from, WrappedNode to) {
        return new WrappedLine(from, to);
    }

    public Line asGraphicalLine() {
        if (line == null) {
            line = Line.builder(from.asGraphicalNode(), to.asGraphicalNode()).build();
        }

        return line;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedLine other
                && Objects.equals(from, other.from)
                && Objects.equals(to, other.to);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
