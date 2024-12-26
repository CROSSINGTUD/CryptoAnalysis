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
import crypto.analysis.AbstractPredicate;
import crypto.analysis.EnsuredPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractParameterQueryOld;
import crypto.listener.IResultsListener;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.Map;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.HasGeneratedPredicateAssertion;
import test.assertions.HasNotGeneratedPredicateAssertion;
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
                    results.asStatementValWeightTable().cellSet()) {
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
            ExtractParameterQueryOld query, BackwardBoomerangResults<Weight.NoWeight> results) {}

    @Override
    public void collectedValues(
            IAnalysisSeed seed, Collection<CallSiteWithExtractedValue> collectedValues) {
        for (Assertion a : assertions) {
            if (a instanceof ExtractedValueAssertion) {
                ExtractedValueAssertion assertion = (ExtractedValueAssertion) a;
                assertion.computedValues(collectedValues);
            }
        }
    }

    @Override
    public void checkedConstraints(
            IAnalysisSeed analysisSeed,
            Collection<ISLConstraint> constraints,
            Collection<AbstractError> errors) {}

    @Override
    public void generatedPredicate(
            IAnalysisSeed fromSeed,
            AbstractPredicate predicate,
            IAnalysisSeed toSeed,
            Statement statement) {
        for (Assertion a : assertions) {
            if (a instanceof HasGeneratedPredicateAssertion) {
                HasGeneratedPredicateAssertion assertion = (HasGeneratedPredicateAssertion) a;

                // TODO from statement
                Collection<Val> values =
                        fromSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                if (assertion.getStatement().equals(statement)) {
                    assertion.reported(values, predicate);
                }
            }

            if (a instanceof HasNotGeneratedPredicateAssertion) {
                HasNotGeneratedPredicateAssertion assertion = (HasNotGeneratedPredicateAssertion) a;

                // TODO from statement
                Collection<Val> values =
                        fromSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                if (assertion.getStatement().equals(statement)) {
                    assertion.reported(values, predicate);
                }
            }

            if (a instanceof HasEnsuredPredicateAssertion) {
                HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) a;

                // TODO from statement
                Collection<Val> values =
                        toSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                if (statement.equals(assertion.getStmt())) {
                    assertion.reported(values, predicate);
                }
            }

            if (a instanceof NotHasEnsuredPredicateAssertion) {
                NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) a;

                // TODO from statement
                Collection<Val> values =
                        toSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                if (statement.equals(assertion.getStmt())) {
                    assertion.reported(values, predicate);
                }
            }
        }
    }

    @Override
    public void ensuredPredicates(
            IAnalysisSeed seed,
            Multimap<Statement, Map.Entry<EnsuredPredicate, Integer>> predicates) {
        for (Assertion a : assertions) {
            if (a instanceof HasEnsuredPredicateAssertion) {
                HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) a;

                if (!predicates.containsKey(assertion.getStmt())) {
                    continue;
                }

                Collection<Val> values =
                        seed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                Collection<Map.Entry<EnsuredPredicate, Integer>> ensuredPreds =
                        predicates.get(assertion.getStmt());

                for (Map.Entry<EnsuredPredicate, Integer> ensPred : ensuredPreds) {
                    assertion.reported(values, ensPred.getKey());
                }
            }

            if (a instanceof NotHasEnsuredPredicateAssertion) {
                NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) a;

                if (!predicates.containsKey(assertion.getStmt())) {
                    continue;
                }

                Collection<Val> values =
                        seed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                Collection<Map.Entry<EnsuredPredicate, Integer>> ensuredPreds =
                        predicates.get(assertion.getStmt());

                for (Map.Entry<EnsuredPredicate, Integer> ensPred : ensuredPreds) {
                    assertion.reported(values, ensPred.getKey());
                }
            }
        }
    }
}
