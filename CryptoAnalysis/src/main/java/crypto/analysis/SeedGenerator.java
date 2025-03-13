package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.definition.Definitions;
import crypto.typestate.ForwardSeedQuery;
import crypto.typestate.TypestateAnalysis;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import typestate.TransitionFunction;

public class SeedGenerator {

    private final CryptoScanner scanner;
    private final FrameworkScope frameworkScope;
    private final TypestateAnalysis typestateAnalysis;

    public SeedGenerator(
            CryptoScanner scanner, FrameworkScope frameworkScope, Collection<CrySLRule> rules) {
        this.scanner = scanner;
        this.frameworkScope = frameworkScope;

        Definitions.TypestateDefinition definition =
                new Definitions.TypestateDefinition(frameworkScope, rules, scanner.getTimeout());
        typestateAnalysis = new TypestateAnalysis(definition);
    }

    public Collection<IAnalysisSeed> computeSeeds() {
        scanner.getAnalysisReporter().beforeTypestateAnalysis();
        typestateAnalysis.runTypestateAnalysis();
        scanner.getAnalysisReporter().afterTypestateAnalysis();

        return extractSeedsFromBoomerangResults();
    }

    private Collection<IAnalysisSeed> extractSeedsFromBoomerangResults() {
        Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> results =
                typestateAnalysis.getResults();
        Collection<IAnalysisSeed> seeds = new HashSet<>();

        for (Map.Entry<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> entry :
                results.entrySet()) {
            ForwardSeedQuery forwardQuery = entry.getKey();
            Statement stmt = forwardQuery.cfgEdge().getStart();
            Val fact = forwardQuery.var();

            IAnalysisSeed seed;
            if (forwardQuery.hasSpecification()) {
                CrySLRule rule = forwardQuery.getRule();

                seed =
                        new AnalysisSeedWithSpecification(
                                scanner, stmt, fact, frameworkScope, entry.getValue(), rule);
            } else {
                seed = new AnalysisSeedWithEnsuredPredicate(scanner, stmt, fact, entry.getValue());
            }
            seeds.add(seed);

            if (entry.getValue().isTimedout()) {
                scanner.getAnalysisReporter().onTypestateAnalysisTimeout(seed);
            }

            scanner.getAnalysisReporter().typestateAnalysisResults(seed, entry.getValue());
        }

        return seeds;
    }
}
