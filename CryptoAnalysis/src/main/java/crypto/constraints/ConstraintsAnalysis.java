package crypto.constraints;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.definition.Definitions;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConstraintsAnalysis {

    public static final Collection<String> predefinedPredicates =
            Set.of("callTo", "noCallTo", "neverTypeOf", "length", "notHardCoded", "instanceOf");

    private final AnalysisSeedWithSpecification seed;
    private final Definitions.ConstraintsDefinition definition;

    private final Collection<Statement> collectedCalls;
    private final Collection<ISLConstraint> reqPredicates;
    private final Collection<ParameterWithExtractedValues> extractedValues;

    public ConstraintsAnalysis(
            AnalysisSeedWithSpecification seed, Definitions.ConstraintsDefinition definition) {
        this.seed = seed;
        this.definition = definition;

        this.collectedCalls = new HashSet<>();
        this.reqPredicates = new HashSet<>();
        this.extractedValues = new HashSet<>();
    }

    public void initialize() {
        initializeCollectedCalls();
        initializeExtractedValues();
        initializeRelevantPredicates();
    }

    public Collection<AbstractConstraintsError> evaluateConstraints() {
        Collection<AbstractConstraintsError> errors = new HashSet<>();
        ;

        for (ISLConstraint cons : seed.getSpecification().getConstraints()) {
            EvaluableConstraint constraint =
                    EvaluableConstraint.getInstance(seed, cons, collectedCalls, extractedValues);
            EvaluableConstraint.EvaluationResult result = constraint.evaluate();

            definition.reporter().onEvaluatedConstraint(seed, constraint, result);

            if (constraint.isViolated()) {
                errors.addAll(constraint.getErrors());
            }
        }

        return errors;
    }

    private void initializeCollectedCalls() {
        collectedCalls.clear();

        Collection<ControlFlowGraph.Edge> edges = seed.getAllCallsOnObject().keySet();
        for (ControlFlowGraph.Edge edge : edges) {
            collectedCalls.add(edge.getStart());
        }
    }

    private void initializeExtractedValues() {
        extractedValues.clear();

        Definitions.ExtractParameterDefinition parameterDefinition =
                new Definitions.ExtractParameterDefinition(
                        definition.callGraph(),
                        definition.dataFlowScope(),
                        definition.timeout(),
                        definition.strategy());
        ExtractParameterAnalysis analysis = new ExtractParameterAnalysis(parameterDefinition);

        Collection<ParameterWithExtractedValues> params =
                analysis.extractParameters(collectedCalls, seed.getSpecification());
        extractedValues.addAll(params);
    }

    private void initializeRelevantPredicates() {
        reqPredicates.clear();
    }
}
