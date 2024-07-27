package crypto.extractparameter.transformation;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.definition.ExtractParameterDefinition;
import crypto.extractparameter.ExtractParameterOptions;
import wpds.impl.Weight;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public abstract class Transformation {

    private final ExtractParameterDefinition definition;

    public Transformation(ExtractParameterDefinition definition) {
        this.definition = definition;
    }

    public abstract Optional<AllocVal> evaluateExpression(Statement statement);

    protected Optional<ForwardQuery> triggerBackwardQuery(Statement statement, Val val) {
        Collection<ForwardQuery> extractedValues = new HashSet<>();

        Collection<Statement> preds = statement.getMethod().getControlFlowGraph().getPredsOf(statement);
        for (Statement pred : preds) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            ExtractParameterOptions options = new ExtractParameterOptions(definition);
            BackwardQuery backwardQuery = BackwardQuery.make(edge, val);
            Boomerang boomerang = new Boomerang(definition.getCallGraph(), definition.getDataFlowScope(), options);

            BackwardBoomerangResults<Weight.NoWeight> results = boomerang.solve(backwardQuery);
            extractedValues.addAll(results.getAllocationSites().keySet());
        }

        // If we have multiple allocation sites, then we cannot correctly evaluate an expression
        if (extractedValues.size() != 1) {
            return Optional.empty();
        }

        ForwardQuery forwardQuery = extractedValues.stream().iterator().next();
        return Optional.of(forwardQuery);
    }
}
