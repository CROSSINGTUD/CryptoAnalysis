package test;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.listener.IResultsListener;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import java.util.Map;
import test.assertions.ConstraintsEvaluatedAssertion;
import test.assertions.ConstraintsNotRelevantAssertion;
import test.assertions.ConstraintsSatisfiedAssertion;
import test.assertions.ConstraintsViolatedAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.StateResult;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import wpds.impl.Weight;

public class UsagePatternResultsListener implements IResultsListener {

    private final Collection<Assertion> assertions;

    public UsagePatternResultsListener(Collection<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public void constructedCallGraph(CallGraph callGraph) {}

    @Override
    public void typestateAnalysisResults(
            IAnalysisSeed analysisSeed, ForwardBoomerangResults<TransitionFunction> results) {
        Multimap<Statement, StateResult> expectedTypestateResults = HashMultimap.create();

        for (Assertion a : assertions) {
            if (a instanceof StateResult) {
                StateResult stateResult = (StateResult) a;
                expectedTypestateResults.put(stateResult.getStmt(), stateResult);
            }
        }

        for (Map.Entry<Statement, StateResult> entry : expectedTypestateResults.entries()) {
            for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> cell :
                    results.asEdgeValWeightTable().cellSet()) {
                Statement expectedStatement = entry.getKey();
                Collection<Val> expectedVal = entry.getValue().getVal();

                Statement analysisResultStatement = cell.getRowKey().getStart();
                Val analysisResultVal = cell.getColumnKey();

                if (!analysisResultStatement.equals(expectedStatement)
                        || !expectedVal.contains(analysisResultVal)) {
                    continue;
                }

                for (ITransition transition : cell.getValue().values()) {
                    if (transition.from() == null || transition.to() == null) {
                        continue;
                    }

                    if (transition.from().isInitialState()) {
                        entry.getValue().computedResults(transition.to());
                    }
                }
            }
        }
    }

    @Override
    public void extractedBoomerangResults(
            ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results) {}

    @Override
    public void extractedParameterValues(
            IAnalysisSeed seed, Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        for (Assertion a : assertions) {
            if (a instanceof ExtractedValueAssertion assertion) {
                assertion.computedValues(extractedValues);
            }
        }
    }

    @Override
    public void evaluatedConstraint(
            IAnalysisSeed seed,
            EvaluableConstraint constraint,
            EvaluableConstraint.EvaluationResult result) {
        for (Assertion assertion : assertions) {
            Collection<Val> values =
                    seed.getAnalysisResults().asStatementValWeightTable().columnKeySet();

            if (assertion instanceof ConstraintsEvaluatedAssertion a) {
                a.reported(values);
            }

            if (assertion instanceof ConstraintsSatisfiedAssertion a) {
                a.reported(values, result);
            }

            if (assertion instanceof ConstraintsViolatedAssertion a) {
                a.reported(values, result);
            }

            if (assertion instanceof ConstraintsNotRelevantAssertion a) {
                a.reported(values, result);
            }
        }
    }

    @Override
    public void ensuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, EnsuredPredicate> predicates) {
        for (Assertion a : assertions) {
            if (a instanceof HasEnsuredPredicateAssertion assertion) {
                Collection<Val> values = seed.getAliasesAtStatement(assertion.getStmt());
                Collection<EnsuredPredicate> ensuredPreds = predicates.get(assertion.getStmt());

                for (EnsuredPredicate ensPred : ensuredPreds) {
                    assertion.reported(values, ensPred);
                }
            }

            if (a instanceof NotHasEnsuredPredicateAssertion assertion) {
                Collection<Val> values = seed.getAliasesAtStatement(assertion.getStmt());
                Collection<EnsuredPredicate> ensuredPreds = predicates.get(assertion.getStmt());

                for (EnsuredPredicate ensPred : ensuredPreds) {
                    assertion.reported(values, ensPred);
                }
            }
        }
    }

    @Override
    public void unEnsuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, UnEnsuredPredicate> predicates) {}
}
