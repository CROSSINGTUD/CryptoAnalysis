package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import crypto.definition.ExtractParameterDefinition;
import wpds.impl.Weight;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ExtractParameterQuery extends BackwardQuery {

    private final ExtractParameterDefinition definition;
    private final Collection<QueryListener> listeners;
    private final int index;

    public ExtractParameterQuery(ExtractParameterDefinition definition, ControlFlowGraph.Edge statement, Val val, int index) {
        super(statement, val);

        this.definition = definition;
        this.index = index;
        this.listeners = new HashSet<>();
    }

    public void solve() {
        ExtractParameterOptions options = new ExtractParameterOptions(definition);
        Boomerang boomerang = new Boomerang(definition.getCallGraph(), definition.getDataFlowScope(), options);

        BackwardBoomerangResults<Weight.NoWeight> results = boomerang.solve(this);

        if (results.isTimedout()) {
            definition.getAnalysisReporter().onExtractParameterAnalysisTimeout(var(), cfgEdge().getTarget());
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
        return Arrays.hashCode(new Object[]{
                super.hashCode(),
                index
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        ExtractParameterQuery other = (ExtractParameterQuery) obj;
        return index == other.index;
    }
}
