package crypto.extractparameter.transformation;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.definition.Definitions;
import crypto.extractparameter.ExtractParameterOptions;
import crypto.extractparameter.scope.IntVal;
import crypto.extractparameter.scope.LongVal;
import crypto.extractparameter.scope.StringVal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import wpds.impl.Weight;

public abstract class Transformation {

    private final Definitions.BoomerangOptionsDefinition definition;

    public Transformation(Definitions.BoomerangOptionsDefinition definition) {
        this.definition = definition;
    }

    public abstract Optional<AllocVal> evaluateExpression(Statement statement);

    protected Optional<String> extractStringFromVal(Statement statement, Val val) {
        Optional<ForwardQuery> forwardQuery = triggerBackwardQuery(statement, val);

        if (forwardQuery.isEmpty()) {
            return Optional.empty();
        }

        return extractStringFromBoomerangResult(forwardQuery.get());
    }

    protected Optional<String> extractStringFromBoomerangResult(ForwardQuery query) {
        Statement statement = query.cfgEdge().getStart();

        if (!statement.isAssign()) {
            return Optional.empty();
        }

        Val rightOp = statement.getRightOp();
        if (!rightOp.isStringConstant()) {
            return Optional.empty();
        }

        return Optional.of(rightOp.getStringValue());
    }

    protected Optional<Long> extractLongFromVal(Statement statement, Val val) {
        Optional<ForwardQuery> forwardQuery = triggerBackwardQuery(statement, val);

        if (forwardQuery.isEmpty()) {
            return Optional.empty();
        }

        return extractLongFromBoomerangResult(forwardQuery.get());
    }

    protected Optional<Long> extractLongFromBoomerangResult(ForwardQuery query) {
        Statement statement = query.cfgEdge().getStart();

        if (!statement.isAssign()) {
            return Optional.empty();
        }

        Val rightOp = statement.getRightOp();
        if (!rightOp.isLongConstant()) {
            return Optional.empty();
        }

        return Optional.of(rightOp.getLongValue());
    }

    protected Optional<ForwardQuery> triggerBackwardQuery(Statement statement, Val val) {
        Collection<ForwardQuery> extractedValues = new HashSet<>();

        Collection<Statement> preds =
                statement.getMethod().getControlFlowGraph().getPredsOf(statement);
        for (Statement pred : preds) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            ExtractParameterOptions options = new ExtractParameterOptions(definition);
            BackwardQuery backwardQuery = BackwardQuery.make(edge, val);
            Boomerang boomerang =
                    new Boomerang(definition.callGraph(), definition.dataFlowScope(), options);

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

    protected AllocVal createTransformedAllocVal(String string, Statement statement) {
        Val leftOp = statement.getLeftOp();
        Val resultOp = new StringVal(string, statement.getMethod());

        return new TransformedAllocVal(leftOp, statement, resultOp);
    }

    protected AllocVal createTransformedAllocVal(int intValue, Statement statement) {
        Val leftOp = statement.getLeftOp();
        Val resultOp = new IntVal(intValue, statement.getMethod());

        return new TransformedAllocVal(leftOp, statement, resultOp);
    }

    protected AllocVal createTransformedAllocVal(long longValue, Statement statement) {
        Val leftOp = statement.getLeftOp();
        Val resultOp = new LongVal(longValue, statement.getMethod());

        return new TransformedAllocVal(leftOp, statement, resultOp);
    }
}
