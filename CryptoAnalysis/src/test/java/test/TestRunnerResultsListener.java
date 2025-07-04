/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test;

import boomerang.BackwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithEnsuredPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.listener.IResultsListener;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import java.util.HashSet;
import test.assertions.Assertion;
import test.assertions.ConstraintsEvaluatedAssertion;
import test.assertions.ConstraintsNotRelevantAssertion;
import test.assertions.ConstraintsSatisfiedAssertion;
import test.assertions.ConstraintsViolatedAssertion;
import test.assertions.DiscoveredPredicateSeedAssertion;
import test.assertions.DiscoveredRuleSeedAssertion;
import test.assertions.DiscoveredSeedAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.states.StateResult;
import typestate.TransitionFunction;
import typestate.finiteautomata.State;
import wpds.impl.NoWeight;

public class TestRunnerResultsListener implements IResultsListener {

    private final Collection<Assertion> assertions;

    public TestRunnerResultsListener(Collection<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public void constructedCallGraph(CallGraph callGraph) {}

    @Override
    public void typestateAnalysisResults(
            IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results) {
        for (Assertion a : assertions) {
            if (a instanceof StateResult stateResult) {
                Statement statement = stateResult.getStmt();

                Collection<Val> values = seed.getAliasesAtStatement(statement);
                if (!values.contains(stateResult.getSeed())) {
                    continue;
                }

                Collection<State> states = seed.getStatesAtStatement(statement);
                stateResult.computedStates(states);
            }
        }
    }

    @Override
    public void discoveredSeeds(Collection<IAnalysisSeed> seeds) {
        Collection<DiscoveredSeedAssertion> seedAssertions = new HashSet<>();
        Collection<DiscoveredRuleSeedAssertion> ruleSeedAssertions = new HashSet<>();
        Collection<DiscoveredPredicateSeedAssertion> predicateSeedAssertions = new HashSet<>();

        for (Assertion a : assertions) {
            if (a instanceof DiscoveredSeedAssertion assertion) {
                seedAssertions.add(assertion);
            }

            if (a instanceof DiscoveredRuleSeedAssertion assertion) {
                ruleSeedAssertions.add(assertion);
            }

            if (a instanceof DiscoveredPredicateSeedAssertion assertion) {
                predicateSeedAssertions.add(assertion);
            }
        }

        for (IAnalysisSeed seed : seeds) {
            if (seed instanceof AnalysisSeedWithSpecification) {
                for (DiscoveredSeedAssertion a : seedAssertions) {
                    a.increaseCount();
                }

                for (DiscoveredRuleSeedAssertion a : ruleSeedAssertions) {
                    a.increaseCount();
                }
            }

            if (seed instanceof AnalysisSeedWithEnsuredPredicate) {
                for (DiscoveredSeedAssertion a : seedAssertions) {
                    a.increaseCount();
                }

                for (DiscoveredPredicateSeedAssertion a : predicateSeedAssertions) {
                    a.increaseCount();
                }
            }
        }
    }

    @Override
    public void extractedBoomerangResults(
            BackwardQuery query, BackwardBoomerangResults<NoWeight> results) {}

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
