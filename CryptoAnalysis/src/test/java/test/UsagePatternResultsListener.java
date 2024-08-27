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
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.listener.IResultsListener;
import crypto.rules.ISLConstraint;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.StateResult;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import wpds.impl.Weight;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UsagePatternResultsListener implements IResultsListener {

    private final Collection<Assertion> assertions;

    public UsagePatternResultsListener(Collection<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public void constructedCallGraph(CallGraph callGraph) {}

    @Override
    public void typestateAnalysisResults(IAnalysisSeed analysisSeed, ForwardBoomerangResults<TransitionFunction> results) {
        Multimap<Statement, StateResult> expectedTypestateResults = HashMultimap.create();

        for (Assertion a : assertions) {
            if (a instanceof StateResult) {
                StateResult stateResult = (StateResult) a;
                expectedTypestateResults.put(stateResult.getStmt(), stateResult);
            }
        }

        for (Map.Entry<Statement, StateResult> entry : expectedTypestateResults.entries()) {
            for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> cell : results.asStatementValWeightTable().cellSet()) {
                Statement expectedStatement = entry.getKey();
                Collection<Val> expectedVal = entry.getValue().getVal();

                Statement analysisResultStatement = cell.getRowKey().getStart();
                Val analysisResultVal = cell.getColumnKey();

                if (!analysisResultStatement.equals(expectedStatement) || !expectedVal.contains(analysisResultVal)) {
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
    public void extractedBoomerangResults(ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results) {}

    @Override
    public void collectedValues(IAnalysisSeed seed, Collection<CallSiteWithExtractedValue> collectedValues) {
        for (Assertion a : assertions) {
            if (a instanceof ExtractedValueAssertion) {
                ExtractedValueAssertion assertion = (ExtractedValueAssertion) a;
                assertion.computedValues(collectedValues);
            }
        }
    }

    @Override
    public void checkedConstraints(IAnalysisSeed analysisSeed, Collection<ISLConstraint> constraints, Collection<AbstractError> errors) {}

    @Override
    public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates) {
        for (Table.Cell<Statement, Val, Set<EnsuredCrySLPredicate>> c : existingPredicates.cellSet()) {
            for (Assertion a : assertions) {
                if (a instanceof HasEnsuredPredicateAssertion) {
                    HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) a;
                    if (assertion.getStmt().equals(c.getRowKey())) {
                        for (EnsuredCrySLPredicate pred : c.getValue()) {
                            assertion.reported(c.getColumnKey(),pred);
                        }
                    }
                }

                if (a instanceof NotHasEnsuredPredicateAssertion) {
                    NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) a;
                    if (assertion.getStmt().equals(c.getRowKey())) {
                        for (EnsuredCrySLPredicate pred : c.getValue()) {
                            assertion.reported(c.getColumnKey(),pred);
                        }
                    }
                }
            }
        }
    }
}
