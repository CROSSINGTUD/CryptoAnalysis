/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.predicates.AbstractPredicate;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.ExpectedPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import typestate.TransitionFunction;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed {

    private final Multimap<Statement, Integer> relevantStatements;
    private final Collection<AbstractPredicate> predicatesToPropagate;

    public AnalysisSeedWithEnsuredPredicate(
            CryptoScanner scanner,
            Statement statement,
            Val fact,
            ForwardBoomerangResults<TransitionFunction> results) {
        super(scanner, statement, fact, results);

        relevantStatements = HashMultimap.create();
        predicatesToPropagate = new HashSet<>();
    }

    @Override
    public void execute() {
        scanner.getAnalysisReporter().onSeedStarted(this);

        scanner.getAnalysisReporter().onSeedFinished(this);
    }

    @Override
    public void registerExpectedPredicate(ExpectedPredicate expectedPredicate) {
        super.registerExpectedPredicate(expectedPredicate);

        /* Since seeds without a rule cannot ensure predicates, they have to get the
         * expected predicate from another (unknown) seed
         */
        for (Statement statement : relevantStatements.keySet()) {
            Collection<Integer> indices = relevantStatements.get(statement);

            for (Integer index : indices) {
                ExpectedPredicate predicate =
                        new ExpectedPredicate(
                                this, expectedPredicate.predicate(), statement, index);

                if (expectedPredicate.statement().equals(statement)) {
                    continue;
                }

                for (AnalysisSeedWithSpecification seed : scanner.getAnalysisSeedsWithSpec()) {
                    Collection<Statement> invokedMethods = seed.getInvokedMethodStatements();

                    if (invokedMethods.contains(statement)) {
                        seed.registerExpectedPredicate(predicate);
                    }
                }
            }
        }
    }

    @Override
    public void beforePredicateChecks(Collection<IAnalysisSeed> seeds) {
        relevantStatements.put(getOrigin(), -1);
        for (Statement statement : statementValWeightTable.rowKeySet()) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            InvokeExpr invokeExpr = statement.getInvokeExpr();
            Collection<Val> values = getAliasesAtStatement(statement);

            for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
                Val param = invokeExpr.getArg(i);

                if (values.contains(param)) {
                    relevantStatements.put(statement, i);
                }
            }
        }
    }

    @Override
    public void propagatePredicates() {
        Collection<Statement> allStatements = new HashSet<>();
        allStatements.addAll(relevantStatements.keySet());
        allStatements.addAll(expectedPredicates.keySet());

        for (Statement statement : allStatements) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            for (AbstractPredicate ensPred : predicatesToPropagate) {
                propagatePredicateAtStatement(ensPred, statement);
            }
        }
    }

    private void propagatePredicateAtStatement(AbstractPredicate predicate, Statement statement) {
        // Ensure the predicate on this seed
        AbstractPredicate generatedPredicate = createPredicateWithIndex(predicate, statement, -1);
        this.onGeneratedPredicate(generatedPredicate);

        if (statement.getInvokeExpr().isStaticInvokeExpr()) {
            return;
        }

        if (statement.equals(getOrigin())) {
            return;
        }

        Collection<Integer> indices = relevantStatements.get(statement);
        for (Integer index : indices) {
            AbstractPredicate predForSeed = createPredicateWithIndex(predicate, statement, index);

            notifyExpectingSeeds(predForSeed);
        }
    }

    private void notifyExpectingSeeds(AbstractPredicate predicate) {
        Statement statement = predicate.getStatement();

        Collection<ExpectedPredicate> expectedPredsAtStatement = expectedPredicates.get(statement);
        for (ExpectedPredicate expectedPred : expectedPredsAtStatement) {
            if (expectedPred.predicate().equals(predicate.getPredicate())
                    && expectedPred.paramIndex() == predicate.getIndex()) {
                IAnalysisSeed seed = expectedPred.seed();

                if (seed instanceof AnalysisSeedWithSpecification seedWithSpec) {
                    seedWithSpec.onGeneratedPredicateFromOtherSeed(predicate);
                }
            }
        }
    }

    private AbstractPredicate createPredicateWithIndex(
            AbstractPredicate predicate, Statement statement, int index) {
        if (predicate instanceof EnsuredPredicate ensPred) {
            return new EnsuredPredicate(
                    ensPred.getGeneratingSeed(), ensPred.getPredicate(), statement, index);
        } else if (predicate instanceof UnEnsuredPredicate unEnsPred) {
            return new UnEnsuredPredicate(
                    unEnsPred.getGeneratingSeed(),
                    unEnsPred.getPredicate(),
                    statement,
                    index,
                    unEnsPred.getViolations());
        } else {
            return predicate;
        }
    }

    public void onPredicateGeneratedFromOtherSeed(AbstractPredicate predicate) {
        Collection<AbstractPredicate> currentPreds = new HashSet<>(predicatesToPropagate);

        for (AbstractPredicate pred : currentPreds) {
            if (pred.getPredicate().equals(predicate.getPredicate())) {
                predicatesToPropagate.remove(pred);
            }
        }

        predicatesToPropagate.add(predicate);
        onGeneratedPredicate(predicate);
    }

    @Override
    public void afterPredicateChecks() {
        scanner.getAnalysisReporter().ensuredPredicates(this, ensuredPredicates);
        scanner.getAnalysisReporter().unEnsuredPredicates(this, unEnsuredPredicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "AnalysisSeedWithoutSpec [" + super.toString() + "]";
    }
}
