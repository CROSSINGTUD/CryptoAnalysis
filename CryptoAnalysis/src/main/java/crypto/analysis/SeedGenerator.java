package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.rules.CrySLRule;
import crypto.typestate.ForwardSeedQuery;
import crypto.typestate.TypestateAnalysis;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class SeedGenerator {

    private final CryptoScanner scanner;
    private final TypestateAnalysis typestateAnalysis;


    public SeedGenerator(CryptoScanner scanner, Collection<CrySLRule> rules) {
        this.scanner = scanner;

        typestateAnalysis = new TypestateAnalysis(rules) {
            @Override
            public CallGraph callGraph() {
                return scanner.callGraph();
            }

            @Override
            public DataFlowScope getDataFlowScope() {
                return scanner.getDataFlowScope();
            }
        };
    }

    public Collection<IAnalysisSeed> computeSeeds() {
        typestateAnalysis.runTypestateAnalysis();

        return extractSeedsFromBoomerangResults();
    }

    private Collection<IAnalysisSeed> extractSeedsFromBoomerangResults() {
        Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> results = typestateAnalysis.getResults();

        if (results == null) {
            return Collections.emptySet();
        }

        Collection<IAnalysisSeed> seeds = new HashSet<>();

        for (Map.Entry<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> entry : results.entrySet()) {
            ForwardSeedQuery forwardQuery = entry.getKey();
            Statement stmt = forwardQuery.cfgEdge().getStart();
            Val fact = forwardQuery.var();

            IAnalysisSeed seed;
            if (forwardQuery.hasSpecification()) {
                CrySLRule rule = forwardQuery.getRule();

                seed = new AnalysisSeedWithSpecification(scanner, stmt, fact, entry.getValue(), rule);
            } else {
                seed = new AnalysisSeedWithEnsuredPredicate(scanner, stmt, fact, entry.getValue());
            }
            seeds.add(seed);
        }

        return seeds;
    }
}
