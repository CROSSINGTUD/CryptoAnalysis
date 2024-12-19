package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import wpds.impl.Weight;

public class ExtractParameterQuery extends BackwardQuery {

    private final ExtractParameterDefinition definition;
    private final Collection<QueryListener> listeners;
    private final int index;

    public ExtractParameterQuery(
            ExtractParameterDefinition definition,
            ControlFlowGraph.Edge statement,
            Val val,
            int index) {
        super(statement, val);

        this.definition = definition;
        this.index = index;
        this.listeners = new HashSet<>();
    }

    public void solve() {
        ExtractParameterOptions options = new ExtractParameterOptions(definition);
        Boomerang boomerang =
                new Boomerang(definition.getCallGraph(), definition.getDataFlowScope(), options);

        BackwardBoomerangResults<Weight.NoWeight> results = boomerang.solve(this);

        if (results.isTimedout()) {
            definition
                    .getAnalysisReporter()
                    .onExtractParameterAnalysisTimeout(var(), cfgEdge().getTarget());
        }
        definition.getAnalysisReporter().extractedBoomerangResults(this, results);

        for (QueryListener listener : listeners) {
            listener.solved(results);
        }
    }

    public void addListener(QueryListener listener) {
        listeners.add(listener);
    }

    public interface QueryListener {
        void solved(BackwardBoomerangResults<Weight.NoWeight> results);
    }

    @Override
    public String toString() {
        return "BackwardQuery for " + var() + " @ " + cfgEdge().getTarget() + " at index " + index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ExtractParameterQuery other
                && index == other.index;
    }
}
