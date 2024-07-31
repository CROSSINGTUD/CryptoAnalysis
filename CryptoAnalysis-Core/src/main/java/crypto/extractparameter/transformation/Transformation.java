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
import crypto.utils.SootUtils;
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

    protected AllocVal createTransformedAllocVal(String string, Statement statement) {
        Val leftOp = statement.getLeftOp();
        Val resultOp = SootUtils.toStringConstant(string, statement.getMethod());

        return new TransformedAllocVal(leftOp, statement, resultOp);
    }

    protected AllocVal createTransformedAllocVal(int intValue, Statement statement) {
        Val leftOp = statement.getLeftOp();
        Val resultOp = SootUtils.toIntConstant(intValue, statement.getMethod());

        return new TransformedAllocVal(leftOp, statement, resultOp);
    }

}
