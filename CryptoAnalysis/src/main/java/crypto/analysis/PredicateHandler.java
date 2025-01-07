package crypto.analysis;

import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AbstractRequiredPredicateError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PredicateHandler {

    private final CryptoScanner cryptoScanner;

    public PredicateHandler(CryptoScanner cryptoScanner) {
        this.cryptoScanner = cryptoScanner;
    }

    public void checkPredicates() {
        runPredicateMechanism();

        // Connections are only available once all errors have been reported
        connectSubsequentErrors();
    }

    private void runPredicateMechanism() {
        for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
            seed.computeExpectedPredicates(cryptoScanner.getDiscoveredSeeds());
        }

        List<AnalysisSeedWithSpecification> sortedSeeds =
                topologicallySortSeeds(cryptoScanner.getAnalysisSeedsWithSpec());

        for (AnalysisSeedWithSpecification seed : sortedSeeds) {
            seed.ensurePredicates();
        }
    }

    private void connectSubsequentErrors() {
        for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
            for (AbstractError error : seed.getErrors()) {
                if (!(error instanceof AbstractRequiredPredicateError reqPredError)) {
                    continue;
                }

                for (UnEnsuredPredicate unEnsuredPredicate : reqPredError.getHiddenPredicates()) {
                    Collection<AbstractError> precedingErrors =
                            unEnsuredPredicate.getPrecedingErrors();

                    precedingErrors.forEach(error::addPrecedingError);
                    precedingErrors.forEach(e -> e.addSubsequentError(error));
                }
            }
        }
    }

    private List<AnalysisSeedWithSpecification> topologicallySortSeeds(
            Collection<AnalysisSeedWithSpecification> seeds) {
        List<AnalysisSeedWithSpecification> result = new ArrayList<>();

        Map<AnalysisSeedWithSpecification, Integer> predecessors = new HashMap<>();
        for (AnalysisSeedWithSpecification seed : seeds) {
            predecessors.put(seed, 0);
        }

        for (AnalysisSeedWithSpecification seed : seeds) {
            for (AnalysisSeedWithSpecification dependantSeed : seed.getRequiringSeeds()) {
                int preds = predecessors.get(dependantSeed);

                predecessors.put(dependantSeed, preds + 1);
            }
        }

        Collection<AnalysisSeedWithSpecification> seedsWithoutPreds =
                getSeedsWithNoPreds(predecessors);
        while (!seedsWithoutPreds.isEmpty()) {
            for (AnalysisSeedWithSpecification seedWithoutPred : seedsWithoutPreds) {
                result.add(seedWithoutPred);

                predecessors.put(seedWithoutPred, -1);
                for (AnalysisSeedWithSpecification dependantSeed :
                        seedWithoutPred.getRequiringSeeds()) {
                    int value = predecessors.get(dependantSeed);

                    predecessors.put(dependantSeed, value - 1);
                }
            }

            seedsWithoutPreds = getSeedsWithNoPreds(predecessors);
        }

        return result;
    }

    private Collection<AnalysisSeedWithSpecification> getSeedsWithNoPreds(
            Map<AnalysisSeedWithSpecification, Integer> seeds) {
        Collection<AnalysisSeedWithSpecification> result = new HashSet<>();

        for (AnalysisSeedWithSpecification seed : seeds.keySet()) {
            if (seeds.get(seed) == 0) {
                result.add(seed);
            }
        }
        return result;
    }
}
