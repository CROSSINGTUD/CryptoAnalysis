package crypto.predicates;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.BackwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CryptoScanner;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.RequiredCryptSLPredicate;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.boomerang.CogniCryptBoomerangOptions;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.INode;
import wpds.impl.PAutomaton;
import wpds.impl.Weight.NoWeight;

public class PredicateHandler {

	private final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<EnsuredCryptSLPredicate>> existingPredicatesObjectBased = HashBasedTable.create();
	private final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicateObjectBased = HashBasedTable.create();
	private final CryptoScanner cryptoScanner;
	
	public PredicateHandler(CryptoScanner cryptoScanner) {
		this.cryptoScanner = cryptoScanner;
	}

	public boolean addNewPred(IAnalysisSeed seedObj, Statement statement, Val seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(statement, seed);
		boolean added = set.add(ensPred);
		assert existingPredicates.get(statement, seed).contains(ensPred);
		if (added) {
			onPredicateAdded(seedObj, statement, seed, ensPred);
		}

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
					for (final ClassSpecification specification : cryptoScanner.getClassSpecifictions()) {
						if (specification.getInvolvedMethods().contains(method)) {
							Boomerang boomerang = new Boomerang(new CogniCryptBoomerangOptions() {
								@Override
								public Optional<AllocVal> getAllocationVal(SootMethod m, Stmt stmt, Val fact,
										BiDiInterproceduralCFG<Unit, SootMethod> icfg) {
									if(stmt.containsInvokeExpr() && stmt instanceof AssignStmt){
										AssignStmt as = (AssignStmt) stmt;
										if(as.getLeftOp().equals(fact.value())){
											if(icfg.getCalleesOfCallAt(stmt).isEmpty())
												return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(as, m)));
											//TODO replace by check if stmt is a seed of specification
											if(stmt.toString().contains("getInstance")){
												return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(),new Statement(as, m)));
											}
										}
									}
									return super.getAllocationVal(m, stmt, fact, icfg);
								}
							}){
								@Override
								public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
									return cryptoScanner.icfg();
								}
							};
							Val val = new Val(base, callerMethod);
							BackwardQuery backwardQuery = new BackwardQuery(statement, val);
							cryptoScanner.getAnalysisListener().boomerangQueryStarted(seedObj, backwardQuery);
							BackwardBoomerangResults<NoWeight> res = boomerang.solve(backwardQuery);
							cryptoScanner.getAnalysisListener().boomerangQueryFinished(seedObj, backwardQuery);
							Map<ForwardQuery, PAutomaton<Statement, INode<Val>>> allocs = res.getAllocationSites();
							for (ForwardQuery p : allocs.keySet()) {
								AnalysisSeedWithSpecification seedWithSpec = cryptoScanner.getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(cryptoScanner, p.stmt(),p.var(),specification));
								seedWithSpec.addEnsuredPredicate(ensPred);
							}
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
					for(Entry<CallSiteWithParamIndex, ExtractedValue> v : seed.getExtractedValues().entries()){
						if(pred.getPred().getInvolvedVarNames().contains(v.getKey().getVarName()) && v.getKey().stmt().equals(pred.getLocation())){	
							cryptoScanner.getAnalysisListener().reportError(new RequiredPredicateError(pred.getPred(), pred.getLocation(), seed.getSpec().getRule(), new CallSiteWithExtractedValue(v.getKey(),v.getValue())));
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
						cryptoScanner.getAnalysisListener().reportError(new PredicateContradictionError(generatingPredicateStmt, null, disPair));
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
