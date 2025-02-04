package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;
import typestate.TransitionFunction;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed {

    private final Multimap<Statement, Integer> relevantStatements;

    public AnalysisSeedWithEnsuredPredicate(
            CryptoScanner scanner,
            Statement statement,
            Val fact,
            ForwardBoomerangResults<TransitionFunction> results) {
        super(scanner, statement, fact, results);

        relevantStatements = HashMultimap.create();
    }

    @Override
    public void execute() {
        scanner.getAnalysisReporter().onSeedStarted(this);

        relevantStatements.put(getOrigin(), -1);
        for (ControlFlowGraph.Edge edge : analysisResults.asEdgeValWeightTable().rowKeySet()) {
            Statement statement = edge.getTarget();

            if (!statement.containsInvokeExpr()) {
                continue;
            }

            Collection<Val> values = analysisResults.asEdgeValWeightTable().row(edge).keySet();

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
    public void expectPredicate(
            Statement statement, CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {
        CrySLPredicate predToBeEnsured;
        if (predicate.isNegated()) {
            predToBeEnsured = predicate.invertNegation();
        } else {
            predToBeEnsured = predicate;
        }

        expectedPredicates.put(
                statement, new ExpectedPredicateOnSeed(predToBeEnsured, seed, paramIndex));

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
                Collection<Val> values =
                        otherSeed.getAnalysisResults().asStatementValWeightTable().columnKeySet();
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

    public void addEnsuredPredicate(AbstractPredicate predicate) {
        for (Statement statement : expectedPredicates.keySet()) {
            Collection<ExpectedPredicateOnSeed> predicateOnSeeds =
                    expectedPredicates.get(statement);

            for (ExpectedPredicateOnSeed predOnSeed : predicateOnSeeds) {
                if (!predOnSeed.predicate().equals(predicate.getPredicate())) {
                    continue;
                }

                if (!(predOnSeed.seed() instanceof AnalysisSeedWithSpecification seedWithSpec)) {
                    continue;
                }

                seedWithSpec.addEnsuredPredicate(predicate, statement, predOnSeed.paramIndex());
            }
        }

        for (Statement statement : relevantStatements.keySet()) {
            scanner.getAnalysisReporter().onGeneratedPredicate(this, predicate, this, statement);
        }
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
