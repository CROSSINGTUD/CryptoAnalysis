package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.rules.ISLConstraint;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PredicateHandler {

	private final class AddPredicateToOtherSeed implements ResultsHandler {
		private final Statement statement;
		private final Val base;
		private final EnsuredCrySLPredicate ensPred;
		private final AnalysisSeedWithSpecification otherSeed;
		private final int paramIndex;

		private AddPredicateToOtherSeed(Statement statement, Val base, EnsuredCrySLPredicate ensPred, AnalysisSeedWithSpecification otherSeed, int paramIndex) {
			this.statement = statement;
			this.base = base;
			this.ensPred = ensPred;
			this.otherSeed = otherSeed;
			this.paramIndex = paramIndex;
		}

		@Override
		public void done(ForwardBoomerangResults<TransitionFunction> results) {
			for (Map.Entry<ControlFlowGraph.Edge, Map<Val, TransitionFunction>> row : results.asStatementValWeightTable().rowMap().entrySet()) {
				if (row.getKey().getStart().equals(statement)) {
					Map<Val, TransitionFunction> entry = row.getValue();

					if (entry.containsKey(base)) {
						otherSeed.addEnsuredPredicate(ensPred, statement, paramIndex);
					}
				}
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((base == null) ? 0 : base.hashCode());
			result = prime * result + ((ensPred == null) ? 0 : ensPred.hashCode());
			result = prime * result + ((otherSeed == null) ? 0 : otherSeed.hashCode());
			result = prime * result + ((statement == null) ? 0 : statement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AddPredicateToOtherSeed other = (AddPredicateToOtherSeed) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (base == null) {
				if (other.base != null)
					return false;
			} else if (!base.equals(other.base))
				return false;
			if (ensPred == null) {
				if (other.ensPred != null)
					return false;
			} else if (!ensPred.equals(other.ensPred))
				return false;
			if (otherSeed == null) {
				if (other.otherSeed != null)
					return false;
			} else if (!otherSeed.equals(other.otherSeed))
				return false;
			if (statement == null) {
                return other.statement == null;
			} else return statement.equals(other.statement);
        }

		private PredicateHandler getOuterType() {
			return PredicateHandler.this;
		}

	}

	private final CryptoScanner cryptoScanner;
	private final Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates = HashBasedTable.create();
	private final Map<AnalysisSeedWithSpecification, List<RequiredPredicateError>> requiredPredicateErrors;

	public PredicateHandler(CryptoScanner cryptoScanner) {
		this.cryptoScanner = cryptoScanner;
		this.requiredPredicateErrors = new HashMap<>();
	}

	public void addNewPred(IAnalysisSeed seedObj, Statement statement, Val variable, EnsuredCrySLPredicate ensPred) {
		Set<EnsuredCrySLPredicate> set = getExistingPredicates(statement, variable);
		boolean added = set.add(ensPred);

		if (added) {
			onPredicateAdded(seedObj, statement, variable, ensPred);
		}
	}
	
	public Set<EnsuredCrySLPredicate> getExistingPredicates(Statement stmt, Val seed) {
		Set<EnsuredCrySLPredicate> set = existingPredicates.get(stmt, seed);
		if (set == null) {
			set = new HashSet<>();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}

	private void onPredicateAdded(IAnalysisSeed seedObj, Statement statement, Val fact, EnsuredCrySLPredicate ensPred) {
		if (seedObj instanceof AnalysisSeedWithSpecification) {
			AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) seedObj;

			if (seedWithSpec.getFact().getVariableName().equals(fact.getVariableName())) {
				seedWithSpec.addEnsuredPredicate(ensPred, statement, -1);
			}
		}

		if (statement.containsInvokeExpr()) {
			InvokeExpr invokeExpr = statement.getInvokeExpr();

			if (invokeExpr.isInstanceInvokeExpr()) {
				Val base = invokeExpr.getBase();

				for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
					Val arg = invokeExpr.getArg(i);

					if (!fact.getVariableName().equals(arg.getVariableName())) {
						continue;
					}

					for (AnalysisSeedWithSpecification otherSeed : cryptoScanner.getAnalysisSeedsWithSpec()) {
						otherSeed.registerResultsHandler(new AddPredicateToOtherSeed(statement, base, ensPred, otherSeed, i));
					}
				}
			}

			if (invokeExpr.isStaticInvokeExpr() && statement.isAssign()) {
				for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
					Val arg = invokeExpr.getArg(i);

					if (!fact.getVariableName().equals(arg.getVariableName())) {
						continue;
					}

					for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
						if (seed.getOrigin().equals(statement)) {
							seed.addEnsuredPredicate(ensPred, statement, i);
						}
					}
				}
			}
		}
	}

	public void checkPredicates() {
		runPredicateMechanism();
		collectMissingRequiredPredicates();
		collectContradictingPredicates();
		reportRequiredPredicateErrors();
		cryptoScanner.getAnalysisReporter().ensuredPredicates(existingPredicates);
	}

	private void runPredicateMechanism() {
		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
			seed.checkConstraintsAndEnsurePredicates();
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

}
