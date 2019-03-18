package crypto.predicates;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.inject.internal.util.Lists;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CryptoScanner;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.RequiredCryptSLPredicate;
import crypto.analysis.ResultsHandler;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import typestate.TransitionFunction;

public class PredicateHandler {

	private final class AddPredicateToOtherSeed implements ResultsHandler {
		private final Statement statement;
		private final Value base;
		private final SootMethod callerMethod;
		private final EnsuredCryptSLPredicate ensPred;
		private final AnalysisSeedWithSpecification secondSeed;

		private AddPredicateToOtherSeed(Statement statement, Value base, SootMethod callerMethod,
				EnsuredCryptSLPredicate ensPred, AnalysisSeedWithSpecification secondSeed) {
			this.statement = statement;
			this.base = base;
			this.callerMethod = callerMethod;
			this.ensPred = ensPred;
			this.secondSeed = secondSeed;
		}

		@Override
		public void done(ForwardBoomerangResults<TransitionFunction> results) {
			if(results.asStatementValWeightTable().row(statement).containsKey(new Val(base, callerMethod))){
				secondSeed.addEnsuredPredicate(ensPred);
			}
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
				if (other.statement != null)
					return false;
			} else if (!statement.equals(other.statement))
				return false;
			return true;
		}

		private PredicateHandler getOuterType() {
			return PredicateHandler.this;
		}
		
	}

	private final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<EnsuredCryptSLPredicate>> existingPredicatesObjectBased = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicateObjectBased = HashBasedTable.create();
	private final CryptoScanner cryptoScanner;
	
	public PredicateHandler(CryptoScanner cryptoScanner) {
		this.cryptoScanner = cryptoScanner;
	}

	public boolean addNewPred(IAnalysisSeed seedObj, Statement statement, Val variable, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(statement, variable);
		boolean added = set.add(ensPred);
		assert existingPredicates.get(statement, variable).contains(ensPred);
		if (added) {
			onPredicateAdded(seedObj, statement, variable, ensPred);
		}
		cryptoScanner.getAnalysisListener().onSecureObjectFound(seedObj);
		Set<EnsuredCryptSLPredicate> predsObjBased = existingPredicatesObjectBased.get(statement, seedObj);
		if (predsObjBased == null)
			predsObjBased = Sets.newHashSet();
		predsObjBased.add(ensPred);
		existingPredicatesObjectBased.put(statement, seedObj, predsObjBased);
		return added;
	}
	
	/**
	 * @return the existingPredicates
	 */
	public Set<EnsuredCryptSLPredicate> getExistingPredicates(Statement stmt, Val seed) {
		Set<EnsuredCryptSLPredicate> set = existingPredicates.get(stmt, seed);
		if (set == null) {
			set = Sets.newHashSet();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}
	

	private void onPredicateAdded(IAnalysisSeed seedObj, Statement statement, Val seed, EnsuredCryptSLPredicate ensPred) {
		if (statement.isCallsite()) {
			InvokeExpr ivexpr = ((Stmt) statement.getUnit().get()).getInvokeExpr();
			if (ivexpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ivexpr;
				SootMethod method = iie.getMethod();
				SootMethod callerMethod = statement.getMethod();
				Value base = iie.getBase();
				boolean paramMatch = false;
				for (Value arg : iie.getArgs()) {
					if (seed.value() != null && seed.value().equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for(AnalysisSeedWithSpecification secondSeed : Lists.newArrayList(cryptoScanner.getAnalysisSeeds())) {
						secondSeed.registerResultsHandler(new AddPredicateToOtherSeed(statement, base, callerMethod, ensPred, secondSeed));
						
					}
				}
			}
			
			if (ivexpr instanceof StaticInvokeExpr && statement.getUnit().get() instanceof AssignStmt) {
				StaticInvokeExpr iie = (StaticInvokeExpr) ivexpr;
				boolean paramMatch = false;
				for (Value arg : iie.getArgs()) {
					if (seed.value() != null && seed.value().equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for(AnalysisSeedWithSpecification spec : Lists.newArrayList(cryptoScanner.getAnalysisSeeds())) {
						if(spec.stmt().equals(statement)) {
							spec.addEnsuredPredicate(ensPred);
						}
					}
				}
			}

		}
	}

	public void expectPredicate(IAnalysisSeed object, Statement stmt, CryptSLPredicate predToBeEnsured) {
		for (Unit succ : cryptoScanner.icfg().getSuccsOf(stmt.getUnit().get())) {
			Set<CryptSLPredicate> set = expectedPredicateObjectBased.get(succ, object);
			if (set == null)
				set = Sets.newHashSet();
			set.add(predToBeEnsured);
			expectedPredicateObjectBased.put(new Statement((Stmt)succ,stmt.getMethod()), object, set);
		}
	}


	public void checkPredicates() {
		checkMissingRequiredPredicates();
		checkForContradictions();
		cryptoScanner.getAnalysisListener().ensuredPredicates(this.existingPredicates, expectedPredicateObjectBased, computeMissingPredicates());
	}
	private void checkMissingRequiredPredicates() {
		for (AnalysisSeedWithSpecification seed : cryptoScanner.getAnalysisSeeds()) {
			Set<RequiredCryptSLPredicate> missingPredicates = seed.getMissingPredicates();
			for(RequiredCryptSLPredicate pred : missingPredicates){
				CryptSLRule rule = seed.getSpec().getRule();
				if (!rule.getPredicates().contains(pred.getPred())){
					for(CallSiteWithParamIndex v : seed.getParameterAnalysis().getAllQuerySites()){
						if(pred.getPred().getInvolvedVarNames().contains(v.getVarName()) && v.stmt().equals(pred.getLocation())){
							cryptoScanner.getAnalysisListener().reportError(seed, new RequiredPredicateError(pred.getPred(), pred.getLocation(), seed.getSpec().getRule(), new CallSiteWithExtractedValue(v, null)));
						}
					}
				}
			}
		}	
	}

	private void checkForContradictions() {
		Set<Entry<CryptSLPredicate, CryptSLPredicate>> contradictionPairs = new HashSet<Entry<CryptSLPredicate, CryptSLPredicate>>();
		for(ClassSpecification c : cryptoScanner.getClassSpecifictions()) {
			CryptSLRule rule = c.getRule();
			for (ISLConstraint cons : rule.getConstraints()) {
				if (cons instanceof CryptSLPredicate && ((CryptSLPredicate) cons).isNegated()) {
					contradictionPairs.add(new SimpleEntry<CryptSLPredicate, CryptSLPredicate>(rule.getPredicates().get(0), ((CryptSLPredicate) cons).setNegated(false)));
				}
			}
		}
		for (Statement generatingPredicateStmt : expectedPredicateObjectBased.rowKeySet()) {
			for (Entry<Val, Set<EnsuredCryptSLPredicate>> exPredCell : existingPredicates.row(generatingPredicateStmt).entrySet()) {
				Set<String> preds = new HashSet<String>();
				for (EnsuredCryptSLPredicate exPred : exPredCell.getValue()) {
					preds.add(exPred.getPredicate().getPredName());
				}
				for (Entry<CryptSLPredicate, CryptSLPredicate> disPair : contradictionPairs) {
					if (preds.contains(disPair.getKey().getPredName()) && preds.contains(disPair.getValue().getPredName())) {
						cryptoScanner.getAnalysisListener().reportError(null, new PredicateContradictionError(generatingPredicateStmt, null, disPair));
					}
				}
			}
		}
	}

	private Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> computeMissingPredicates() {
		Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> res = HashBasedTable.create();
		for (Cell<Statement, IAnalysisSeed, Set<CryptSLPredicate>> c : expectedPredicateObjectBased.cellSet()) {
			Set<EnsuredCryptSLPredicate> exPreds = existingPredicatesObjectBased.get(c.getRowKey(), c.getColumnKey());
			if (c.getValue() == null)
				continue;
			HashSet<CryptSLPredicate> expectedPreds = new HashSet<>(c.getValue());
			if (exPreds == null) {
				exPreds = Sets.newHashSet();
			}
			for (EnsuredCryptSLPredicate p : exPreds) {
				expectedPreds.remove(p.getPredicate());
			}
			if (!expectedPreds.isEmpty()) {
				res.put(c.getRowKey(), c.getColumnKey(), expectedPreds);
			}
		}
		return res;
	}

}
