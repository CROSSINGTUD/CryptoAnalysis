package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import java.util.Objects;

public class ExtractParameterQuery extends BackwardQuery {

    private final int index;
    private final String varNameInSpec;

    public ExtractParameterQuery(
            ControlFlowGraph.Edge edge, Val param, int index, String varNameInSpec) {
        super(edge, param);

        this.index = index;
        this.varNameInSpec = varNameInSpec;
    }

    public int getIndex() {
        return index;
    }

    public String getVarNameInSpec() {
        return varNameInSpec;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index, varNameInSpec);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ExtractParameterQuery other
                && index == other.getIndex()
                && Objects.equals(varNameInSpec, other.getVarNameInSpec());
    }
}
