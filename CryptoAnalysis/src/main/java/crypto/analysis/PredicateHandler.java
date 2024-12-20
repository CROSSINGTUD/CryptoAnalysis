package crypto.analysis;

import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AbstractRequiredPredicateError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PredicateHandler {

    private final CryptoScanner cryptoScanner;
    private final Map<AnalysisSeedWithSpecification, List<AbstractRequiredPredicateError>>
            requiredPredicateErrors;

    public PredicateHandler(CryptoScanner cryptoScanner) {
        this.cryptoScanner = cryptoScanner;
        this.requiredPredicateErrors = new HashMap<>();
    }

    public void checkPredicates() {
        runPredicateMechanism();
        collectContradictingPredicates();
        collectMissingRequiredPredicates();
        reportRequiredPredicateErrors();

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

    private void collectContradictingPredicates() {
        for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
            Collection<RequiredCrySLPredicate> contradictedPredicates =
                    seed.computeContradictedPredicates();

            for (RequiredCrySLPredicate pred : contradictedPredicates) {
                PredicateContradictionError error =
                        new PredicateContradictionError(
                                seed, pred.getLocation(), seed.getSpecification(), pred.getPred());
                seed.addError(error);
                cryptoScanner.getAnalysisReporter().reportError(seed, error);
            }
        }
    }

    private void collectMissingRequiredPredicates() {
        for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
            requiredPredicateErrors.put(seed, new ArrayList<>());

            Collection<RequiredCrySLPredicate> violatedReqPreds =
                    seed.computeViolatedRequiredPredicates();
            for (RequiredCrySLPredicate reqPred : violatedReqPreds) {
                Collection<UnEnsuredPredicate> hiddenPreds = seed.extractHiddenPredicates(reqPred);

                RequiredPredicateError reqPredError =
                        new RequiredPredicateError(seed, reqPred, hiddenPreds);
                requiredPredicateErrors.get(seed).add(reqPredError);
            }

            Collection<AlternativeReqPredicate> violatedAltPreds =
                    seed.computeViolatedAlternativePredicates();
            for (AlternativeReqPredicate altPred : violatedAltPreds) {
                Collection<UnEnsuredPredicate> hiddenPreds = seed.extractHiddenPredicates(altPred);

                AlternativeReqPredicateError reqPredError =
                        new AlternativeReqPredicateError(seed, altPred, hiddenPreds);
                requiredPredicateErrors.get(seed).add(reqPredError);
            }
        }
    }

    private void reportRequiredPredicateErrors() {
        for (AnalysisSeedWithSpecification seed : requiredPredicateErrors.keySet()) {
            Collection<AbstractRequiredPredicateError> errors = requiredPredicateErrors.get(seed);

            for (AbstractRequiredPredicateError reqPredError : errors) {
                seed.addError(reqPredError);
                cryptoScanner.getAnalysisReporter().reportError(seed, reqPredError);
            }
        }
    }

    private void connectSubsequentErrors() {
        for (AnalysisSeedWithSpecification seed : requiredPredicateErrors.keySet()) {
            Collection<AbstractRequiredPredicateError> errors = requiredPredicateErrors.get(seed);

            for (AbstractRequiredPredicateError error : errors) {
                for (UnEnsuredPredicate unEnsuredPredicate : error.getHiddenPredicates()) {
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
