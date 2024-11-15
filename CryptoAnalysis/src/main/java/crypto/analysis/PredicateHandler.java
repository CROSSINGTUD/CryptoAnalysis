package crypto.analysis;

import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.rules.ISLConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PredicateHandler {

	private final CryptoScanner cryptoScanner;
	private final Map<AnalysisSeedWithSpecification, List<RequiredPredicateError>> requiredPredicateErrors;

	public PredicateHandler(CryptoScanner cryptoScanner) {
		this.cryptoScanner = cryptoScanner;
		this.requiredPredicateErrors = new HashMap<>();
	}

	public void checkPredicates() {
		runPredicateMechanism();
		collectMissingRequiredPredicates();
		collectContradictingPredicates();
		reportRequiredPredicateErrors();
	}

	private void runPredicateMechanism() {

		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
			seed.computeExpectedPredicates(cryptoScanner.getDiscoveredSeeds());
		}

		List<AnalysisSeedWithSpecification> sortedSeeds = topologicallySortSeeds(cryptoScanner.getAnalysisSeedsWithSpec());

		for (AnalysisSeedWithSpecification seed : sortedSeeds) {
			seed.ensurePredicates();
		}
	}

	private void collectMissingRequiredPredicates() {
		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
			requiredPredicateErrors.put(seed, new ArrayList<>());
			Collection<ISLConstraint> missingPredicates = seed.computeMissingPredicates();

			for (ISLConstraint pred : missingPredicates) {
				if (pred instanceof RequiredCrySLPredicate) {
					RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) pred;

					RequiredPredicateError reqPredError = new RequiredPredicateError(seed, reqPred);
					addRequiredPredicateErrorOnSeed(reqPredError, seed);
				} else if (pred instanceof AlternativeReqPredicate) {
					AlternativeReqPredicate altReqPred = (AlternativeReqPredicate) pred;

					RequiredPredicateError reqPredError = new RequiredPredicateError(seed, altReqPred);
					addRequiredPredicateErrorOnSeed(reqPredError, seed);
				}
			}
		}
	}

	private void addRequiredPredicateErrorOnSeed(RequiredPredicateError reqPredError, AnalysisSeedWithSpecification seed) {
		seed.addHiddenPredicatesToError(reqPredError);
		seed.addError(reqPredError);
		requiredPredicateErrors.get(seed).add(reqPredError);
	}

	private void reportRequiredPredicateErrors() {
		for (Map.Entry<AnalysisSeedWithSpecification, List<RequiredPredicateError>> entry : requiredPredicateErrors.entrySet()) {
			AnalysisSeedWithSpecification seed = entry.getKey();

			for (RequiredPredicateError reqPredError : entry.getValue()) {
				reqPredError.mapPrecedingErrors();
				cryptoScanner.getAnalysisReporter().reportError(seed, reqPredError);
			}
		}
	}

	private void collectContradictingPredicates() {
		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
			Collection<RequiredCrySLPredicate> contradictedPredicates = seed.computeContradictedPredicates();

			for (RequiredCrySLPredicate pred : contradictedPredicates) {
				PredicateContradictionError error = new PredicateContradictionError(seed, pred.getLocation(), seed.getSpecification(), pred.getPred());
				seed.addError(error);
				cryptoScanner.getAnalysisReporter().reportError(seed, error);
			}
		}
	}

	private List<AnalysisSeedWithSpecification> topologicallySortSeeds(Collection<AnalysisSeedWithSpecification> seeds) {
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

		Collection<AnalysisSeedWithSpecification> seedsWithoutPreds = getSeedsWithNoPreds(predecessors);
		while (!seedsWithoutPreds.isEmpty()) {
			for (AnalysisSeedWithSpecification seedWithoutPred : seedsWithoutPreds) {
				result.add(seedWithoutPred);

				predecessors.put(seedWithoutPred, -1);
				for (AnalysisSeedWithSpecification dependantSeed : seedWithoutPred.getRequiringSeeds()) {
					int value = predecessors.get(dependantSeed);

					predecessors.put(dependantSeed, value - 1);
				}
			}

			seedsWithoutPreds = getSeedsWithNoPreds(predecessors);
		}

		return result;
	}

	private Collection<AnalysisSeedWithSpecification> getSeedsWithNoPreds(Map<AnalysisSeedWithSpecification, Integer> seeds) {
		Collection<AnalysisSeedWithSpecification> result = new HashSet<>();

		for (AnalysisSeedWithSpecification seed : seeds.keySet()) {
			if (seeds.get(seed) == 0) {
				result.add(seed);
			}
		}
		return result;
	}

}
