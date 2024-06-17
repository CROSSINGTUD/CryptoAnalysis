package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import typestate.TransitionFunction;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PredicateHandler {

	private final class AddPredicateToOtherSeed implements ResultsHandler {
		private final Statement statement;
		private final Val base;
		private final Method callerMethod;
		private final EnsuredCrySLPredicate ensPred;
		private final AnalysisSeedWithSpecification secondSeed;

		private AddPredicateToOtherSeed(Statement statement, Val base, Method callerMethod, EnsuredCrySLPredicate ensPred, AnalysisSeedWithSpecification secondSeed) {
			this.statement = statement;
			this.base = base;
			this.callerMethod = callerMethod;
			this.ensPred = ensPred;
			this.secondSeed = secondSeed;
		}

		@Override
		public void done(ForwardBoomerangResults<TransitionFunction> results) {
			for (Entry<ControlFlowGraph.Edge, Map<Val, TransitionFunction>> row : results.asStatementValWeightTable().rowMap().entrySet()) {
				if (row.getKey().getStart().equals(statement)) {
					Map<Val, TransitionFunction> entry = row.getValue();

					if (entry.containsKey(base)) {
						secondSeed.addEnsuredPredicate(ensPred);
					}
				}
			}
			/*if (results.asStatementValWeightTable().row(statement).containsKey(base)) {
				secondSeed.addEnsuredPredicate(ensPred);
			}*/
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((base == null) ? 0 : base.hashCode());
			result = prime * result + ((callerMethod == null) ? 0 : callerMethod.hashCode());
			result = prime * result + ((ensPred == null) ? 0 : ensPred.hashCode());
			result = prime * result + ((secondSeed == null) ? 0 : secondSeed.hashCode());
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
			if (callerMethod == null) {
				if (other.callerMethod != null)
					return false;
			} else if (!callerMethod.equals(other.callerMethod))
				return false;
			if (ensPred == null) {
				if (other.ensPred != null)
					return false;
			} else if (!ensPred.equals(other.ensPred))
				return false;
			if (secondSeed == null) {
				if (other.secondSeed != null)
					return false;
			} else if (!secondSeed.equals(other.secondSeed))
				return false;
			if (statement == null) {
                return other.statement == null;
			} else return statement.equals(other.statement);
        }

		private PredicateHandler getOuterType() {
			return PredicateHandler.this;
		}

	}

	private final Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<EnsuredCrySLPredicate>> existingPredicatesObjectBased = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicateObjectBased = HashBasedTable.create();
	private final CryptoScanner cryptoScanner;
	private final Map<AnalysisSeedWithSpecification, List<RequiredPredicateError>> requiredPredicateErrors;

	public PredicateHandler(CryptoScanner cryptoScanner) {
		this.cryptoScanner = cryptoScanner;
		this.requiredPredicateErrors = new HashMap<>();
	}

	public boolean addNewPred(IAnalysisSeed seedObj, Statement statement, Val variable, EnsuredCrySLPredicate ensPred) {
		Set<EnsuredCrySLPredicate> set = getExistingPredicates(statement, variable);
		boolean added = set.add(ensPred);

		if (added) {
			onPredicateAdded(seedObj, statement, variable, ensPred);
		}

		Set<EnsuredCrySLPredicate> predsObjBased = existingPredicatesObjectBased.get(statement, seedObj);
		if (predsObjBased == null)
			predsObjBased = Sets.newHashSet();
		predsObjBased.add(ensPred);
		existingPredicatesObjectBased.put(statement, seedObj, predsObjBased);
		return added;
	}
	
	public Set<EnsuredCrySLPredicate> getExistingPredicates(Statement stmt, Val seed) {
		Set<EnsuredCrySLPredicate> set = existingPredicates.get(stmt, seed);
		if (set == null) {
			set = Sets.newHashSet();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}

	private void onPredicateAdded(IAnalysisSeed seedObj, Statement statement, Val seed, EnsuredCrySLPredicate ensPred) {
		if (statement.containsInvokeExpr()) {
			InvokeExpr invokeExpr = statement.getInvokeExpr();

			if (invokeExpr.isInstanceInvokeExpr()) {
				Method callerMethod = statement.getMethod();
				Val base = invokeExpr.getBase();
				boolean paramMatch = false;
				for (Val arg : invokeExpr.getArgs()) {
					if (!seed.isNull() && seed.getType().equals(arg.getType()) && seed.getVariableName().equals(arg.getVariableName())) {
						paramMatch = true;
					}
				}
				if (paramMatch) {
					for (AnalysisSeedWithSpecification secondSeed : cryptoScanner.getAnalysisSeedsWithSpec()) {
						secondSeed.registerResultsHandler(new AddPredicateToOtherSeed(statement, base, callerMethod, ensPred, secondSeed));
					}
				}
			}

			if (invokeExpr.isStaticInvokeExpr() && statement.isAssign()) {
				boolean paramMatch = false;
				for (Val arg : invokeExpr.getArgs()) {
					if (!seed.isNull() && seed.equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for (AnalysisSeedWithSpecification spec : cryptoScanner.getAnalysisSeedsWithSpec()) {
						if (spec.getOrigin().equals(statement)) {
							spec.addEnsuredPredicate(ensPred);
						}
					}
				}
			}

		}
	}

	public void expectPredicate(IAnalysisSeed object, Statement stmt, CrySLPredicate predToBeEnsured) {
		for (Statement successor : stmt.getMethod().getControlFlowGraph().getSuccsOf(stmt)) {
			Set<CrySLPredicate> set = expectedPredicateObjectBased.get(successor, object);
			if (set == null)
				set = Sets.newHashSet();
			set.add(predToBeEnsured);
			expectedPredicateObjectBased.put(stmt, object, set);
		}
	}

	public void checkPredicates() {
		collectMissingRequiredPredicates();
		reportRequiredPredicateErrors();
		checkForContradictions();
		cryptoScanner.getAnalysisReporter().ensuredPredicates(existingPredicates);
		//cryptoScanner.getAnalysisListener().ensuredPredicates(this.existingPredicates, expectedPredicateObjectBased, computeMissingPredicates());
	}

	private void collectMissingRequiredPredicates() {
		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeedsWithSpec()) {
			requiredPredicateErrors.put(seed, new ArrayList<>());
			Collection<ISLConstraint> missingPredicates = seed.checkPredicates();

			for (ISLConstraint pred : missingPredicates) {
				if (pred instanceof RequiredCrySLPredicate) {
					collectMissingPred(seed, (RequiredCrySLPredicate) pred);
				} else if (pred instanceof AlternativeReqPredicate) {
					collectMissingPred(seed, (AlternativeReqPredicate) pred);
				}
			}
		}
	}

	private void collectMissingPred(AnalysisSeedWithSpecification seed, RequiredCrySLPredicate missingPred) {
		// Check for predicate errors with 'this' as parameter
		if (missingPred.getPred().getParameters().stream().anyMatch(param -> param instanceof CrySLObject && param.getName().equals("this"))) {
			RequiredPredicateError reqPredError = new RequiredPredicateError(Collections.singletonList(missingPred.getPred()), missingPred.getLocation(), seed.getSpecification(), new CallSiteWithExtractedValue(new CallSiteWithParamIndex(missingPred.getLocation(), null, -1, "this"), null));
			addRequiredPredicateErrorOnSeed(reqPredError, seed);

			return;
		}

		for (CallSiteWithParamIndex v : seed.getParameterAnalysis().getAllQuerySites()) {
			if (missingPred.getPred().getInvolvedVarNames().contains(v.getVarName()) && v.stmt().equals(missingPred.getLocation())) {
				RequiredPredicateError reqPredError = new RequiredPredicateError(Collections.singletonList(missingPred.getPred()), missingPred.getLocation(), seed.getSpecification(), new CallSiteWithExtractedValue(v, null));
				addRequiredPredicateErrorOnSeed(reqPredError, seed);
			}
		}
	}
	
	private void collectMissingPred(AnalysisSeedWithSpecification seed, AlternativeReqPredicate missingPred) {
		// Check for predicate errors with 'this' as parameter in all alternatives
		if (missingPred.getAlternatives().parallelStream().anyMatch(p -> p.getParameters().stream().anyMatch(param -> param instanceof CrySLObject && param.getName().equals("this")))) {
			RequiredPredicateError reqPredError = new RequiredPredicateError(missingPred.getAlternatives(), missingPred.getLocation(), seed.getSpecification(), new CallSiteWithExtractedValue(new CallSiteWithParamIndex(missingPred.getLocation(), null, -1, "this"), null));
			addRequiredPredicateErrorOnSeed(reqPredError, seed);

			return;
		}

		for (CallSiteWithParamIndex v : seed.getParameterAnalysis().getAllQuerySites()) {
			if (missingPred.getAlternatives().parallelStream().anyMatch(e -> e.getInvolvedVarNames().contains(v.getVarName())) && v.stmt().equals(missingPred.getLocation())) {
				RequiredPredicateError reqPredError = new RequiredPredicateError(missingPred.getAlternatives(), missingPred.getLocation(), seed.getSpecification(), new CallSiteWithExtractedValue(v, null));
				addRequiredPredicateErrorOnSeed(reqPredError, seed);
			}
		}
	}

	private void addRequiredPredicateErrorOnSeed(RequiredPredicateError reqPredError, AnalysisSeedWithSpecification seed) {
		seed.addHiddenPredicatesToError(reqPredError);
		seed.addError(reqPredError);
		requiredPredicateErrors.get(seed).add(reqPredError);
	}

	private void reportRequiredPredicateErrors() {
		for (Entry<AnalysisSeedWithSpecification, List<RequiredPredicateError>> entry : requiredPredicateErrors.entrySet()) {
			AnalysisSeedWithSpecification seed = entry.getKey();

			for (RequiredPredicateError reqPredError : entry.getValue()) {
				reqPredError.mapPrecedingErrors();
				cryptoScanner.getAnalysisReporter().reportError(seed, reqPredError);
			}
		}
	}

	private void checkForContradictions() {
		Set<Entry<CrySLPredicate, CrySLPredicate>> contradictionPairs = new HashSet<>();
		for (CrySLRule rule : cryptoScanner.getRuleset()) {
			if(!rule.getPredicates().isEmpty()) {
				for (ISLConstraint cons : rule.getConstraints()) {
					if (cons instanceof CrySLPredicate && ((CrySLPredicate) cons).isNegated()) {
						// TODO This is weird; why is it always get(0)?
						contradictionPairs.add(new SimpleEntry<CrySLPredicate, CrySLPredicate>(rule.getPredicates().get(0), ((CrySLPredicate) cons).setNegated(false)));
					}
				}
			}
		}
		for (Statement generatingPredicateStmt : expectedPredicateObjectBased.rowKeySet()) {
			for (Entry<Val, Set<EnsuredCrySLPredicate>> exPredCell : existingPredicates.row(generatingPredicateStmt).entrySet()) {
				Set<String> preds = new HashSet<String>();
				for (EnsuredCrySLPredicate exPred : exPredCell.getValue()) {
					preds.add(exPred.getPredicate().getPredName());
				}
				for (Entry<CrySLPredicate, CrySLPredicate> disPair : contradictionPairs) {
					if (preds.contains(disPair.getKey().getPredName()) && preds.contains(disPair.getValue().getPredName())) {
						// TODO Rule should not be null
						//cryptoScanner.getAnalysisListener().reportError(null, new PredicateContradictionError(generatingPredicateStmt, null, disPair));
					}
				}
			}
		}
	}

	private Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> computeMissingPredicates() {
		Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> res = HashBasedTable.create();
		for (Cell<Statement, IAnalysisSeed, Set<CrySLPredicate>> c : expectedPredicateObjectBased.cellSet()) {
			Set<EnsuredCrySLPredicate> exPreds = existingPredicatesObjectBased.get(c.getRowKey(), c.getColumnKey());
			if (c.getValue() == null)
				continue;
			HashSet<CrySLPredicate> expectedPreds = new HashSet<>(c.getValue());
			if (exPreds == null) {
				exPreds = Sets.newHashSet();
			}
			for (EnsuredCrySLPredicate p : exPreds) {
				expectedPreds.remove(p.getPredicate());
			}
			if (!expectedPreds.isEmpty()) {
				res.put(c.getRowKey(), c.getColumnKey(), expectedPreds);
			}
		}
		return res;
	}
	
}
