package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.rules.CrySLPredicate;
import typestate.TransitionFunction;

import java.util.Collection;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed {

    private final Multimap<Statement, Integer> relevantStatements;

    public AnalysisSeedWithEnsuredPredicate(CryptoScanner scanner, Statement statement, Val fact, ForwardBoomerangResults<TransitionFunction> results) {
        super(scanner, statement, fact, results);

        relevantStatements = HashMultimap.create();
    }

    @Override
    public void execute() {
        scanner.getAnalysisReporter().onSeedStarted(this);

        relevantStatements.put(getOrigin(), -1);
        for (ControlFlowGraph.Edge edge : analysisResults.asStatementValWeightTable().rowKeySet()) {
            Statement statement = edge.getTarget();

            if (!statement.containsInvokeExpr()) {
                continue;
            }

            Collection<Val> values = analysisResults.asStatementValWeightTable().row(edge).keySet();

            InvokeExpr invokeExpr = statement.getInvokeExpr();
            for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
                Val param = invokeExpr.getArg(i);

                if (values.contains(param)) {
                    relevantStatements.put(statement, i);
                }
            }
        }

        scanner.getAnalysisReporter().onSeedFinished(this);
    }

    @Override
    public void expectPredicate(Statement statement, CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {
        CrySLPredicate predToBeEnsured;
        if (predicate.isNegated()) {
            predToBeEnsured = predicate.invertNegation();
        } else {
            predToBeEnsured = predicate;
        }

        expectedPredicates.put(statement, new ExpectedPredicateOnSeed(predToBeEnsured, seed, paramIndex));

        for (Statement relStatement : relevantStatements.keySet()) {
            if (!relStatement.containsInvokeExpr()) {
                continue;
            }

            if (relStatement.equals(statement)) {
                continue;
            }

            InvokeExpr invokeExpr = relStatement.getInvokeExpr();
            if (invokeExpr.isStaticInvokeExpr()) {
                continue;
            }

            Val base = invokeExpr.getBase();
            for (AnalysisSeedWithSpecification otherSeed : scanner.getAnalysisSeedsWithSpec()) {
                if (otherSeed.equals(seed)) {
                    continue;
                }

                // TODO from statement
                Collection<Val> values = otherSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
                if (values.contains(base)) {
                    for (Integer index : relevantStatements.get(relStatement)) {

                        if (otherSeed.canEnsurePredicate(predToBeEnsured, relStatement, index)) {
                            otherSeed.expectPredicate(relStatement, predToBeEnsured, this, index);

                            if (seed instanceof AnalysisSeedWithSpecification) {
                                otherSeed.addRequiringSeed((AnalysisSeedWithSpecification) seed);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addEnsuredPredicate(EnsuredCrySLPredicate predicate) {
        for (Statement statement : expectedPredicates.keySet()) {
            Collection<ExpectedPredicateOnSeed> predicateOnSeeds = expectedPredicates.get(statement);

            for (ExpectedPredicateOnSeed predOnSeed : predicateOnSeeds) {
                if (!predOnSeed.getPredicate().equals(predicate.getPredicate())) {
                    continue;
                }

                if (!(predOnSeed.getSeed() instanceof AnalysisSeedWithSpecification)) {
                    continue;
                }

                AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) predOnSeed.getSeed();
                seedWithSpec.addEnsuredPredicate(predicate, statement, predOnSeed.getParamIndex());
            }
        }

        for (Statement statement : relevantStatements.keySet()) {
            scanner.getAnalysisReporter().onGeneratedPredicate(this, predicate, this, statement);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
