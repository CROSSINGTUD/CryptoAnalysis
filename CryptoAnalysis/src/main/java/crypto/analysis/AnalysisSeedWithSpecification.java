package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class AnalysisSeedWithSpecification implements IAnalysisSeed {
	private final IFactAtStatement factAtStmt;
	private final SootMethod method;
	private final ClassSpecification spec;
	private CryptoScanner cryptoScanner;
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private Multimap<String, String> parametersToValues = HashMultimap.create();
	private CryptoTypestateAnaylsisProblem problem;
	private HashBasedTable<Unit, AccessGraph, TypestateDomainValue<StateNode>> results;
	private boolean solved;
	private Collection<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private Multimap<Unit, StateNode> typeStateChange = HashMultimap.create();

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt, SootMethod method,
			ClassSpecification spec) {
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
		this.method = method;
		this.spec = spec;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spec == null) ? 0 : spec.hashCode());
		result = prime * result + ((factAtStmt == null) ? 0 : factAtStmt.hashCode());
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
		AnalysisSeedWithSpecification other = (AnalysisSeedWithSpecification) obj;
		if (spec == null) {
			if (other.spec != null)
				return false;
		} else if (!spec.equals(other.spec))
			return false;
		if (factAtStmt == null) {
			if (other.factAtStmt != null)
				return false;
		} else if (!factAtStmt.equals(other.factAtStmt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnalysisSeed [" + factAtStmt + " in " + method + " with spec " + spec.getRule().getClassName() + "]";
	}

	public void execute() {
		if (!solved) {
			getOrCreateAnalysis(new ResultReporter<TypestateDomainValue<StateNode>>() {
				@Override
				public void onSeedFinished(IFactAtStatement seed,
						AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
					parametersToValues = convertToStringMultiMap(problem.getCollectedValues());
					cryptoScanner.analysisListener().onSeedFinished(seed, solver);
					AnalysisSeedWithSpecification.this.onSeedFinished(seed, solver);
				}

				@Override
				public void onSeedTimeout(IFactAtStatement seed) {
				}
			}).analysisForSeed(this);

			// TODO Stefan: All method that are invoked on an object can be
			// retrieved like this:
			problem.getInvokedMethodOnInstance();
			cryptoScanner.analysisListener().collectedValues(this, problem.getCollectedValues());
			solved = true;
		}
	}

	

	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		// Merge all information (all access graph here point to the seed
		// object)
		results = solver.results();
		Multimap<Unit, StateNode> unitToStates = HashMultimap.create();
		for (Cell<Unit, AccessGraph, TypestateDomainValue<StateNode>> c : results.cellSet()) {
			unitToStates.putAll(c.getRowKey(), c.getValue().getStates());
		}
		for (Unit curr : unitToStates.keySet()) {
			Collection<StateNode> stateAtCurrMinusPred = Sets.newHashSet(unitToStates.get(curr));
			for (Unit pred : cryptoScanner.icfg().getPredsOf(curr)) {
				Collection<StateNode> stateAtPred = unitToStates.get(pred);
				stateAtCurrMinusPred.removeAll(stateAtPred);
				for (StateNode newStateAtCurr : stateAtCurrMinusPred) {
					typeStateChangeAtStatement(pred, newStateAtCurr);
				}
			}
		}
	}

	private void typeStateChangeAtStatement(Unit curr, StateNode stateNode) {
		typeStateChange.put(curr, stateNode);
		onAddedTypestateChange(curr, stateNode);
	}

	private void onAddedTypestateChange(Unit curr, StateNode stateNode) {
		for (CryptSLPredicate predToBeEnsured : spec.getRule().getPredicates()) {
			if (predToBeEnsured instanceof CryptSLCondPredicate
					&& ((CryptSLCondPredicate) predToBeEnsured).getConditionalMethods().contains(stateNode)
					|| stateNode.getAccepting()) {
				ensuresPred(predToBeEnsured, curr, stateNode);
			}
		}
	}

	private void ensuresPred(CryptSLPredicate predToBeEnsured, Unit currStmt, StateNode stateNode) {
		if (predToBeEnsured.isNegated()) {
			// for (EnsuredCryptSLPredicate ensPred :
			// cryptoScanner.getExistingPredicates(curStmt, this)) {
			// if (ensPred.getPredicate().equals(predToBeEnsured)) {
			// cryptoScanner.deleteNewPred(curStmt, this, ensPred);
			// }
			// }
		} else {
			if (checkConstraintSystem()) {
				System.out.println("ensurePredicate here " + currStmt + stateNode + predToBeEnsured);
				if (predToBeEnsured.getParameters().get(0).getName().equals("this")) {
					for (Cell<Unit, AccessGraph, TypestateDomainValue<StateNode>> e : results.cellSet()) {
						// TODO check for any reachable state that don't kill
						// predicates.
						if (e.getValue().getStates().contains(stateNode)) {
							cryptoScanner.addNewPred(e.getRowKey(), e.getColumnKey(),
									new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
						}
					}
				} else {
					if (currStmt instanceof Stmt && ((Stmt) currStmt).containsInvokeExpr()) {
						 InvokeExpr ie = ((Stmt) currStmt).getInvokeExpr();
						 SootMethod invokedMethod = ie.getMethod();
						 Collection<CryptSLMethod> convert = CryptSLMethodToSootMethod.v().convert(invokedMethod);
						 int i = 0;
						 for(CryptSLMethod cryptSLMethod : convert){
							 for(Entry<String, String> p : cryptSLMethod.getParameters()){
								 if(p.getKey().equals(predToBeEnsured.getParameters().get(0).getName())){
									 if(i == 0){
										 if(currStmt instanceof AssignStmt){
											AssignStmt as = (AssignStmt) currStmt;
											Value leftOp = as.getLeftOp();
											AccessGraph accessGraph = new AccessGraph((Local) leftOp, leftOp.getType());
											AnalysisSeedWithEnsuredPredicate seed = cryptoScanner
													.getOrCreateSeed(new FactAtStatement(currStmt, accessGraph));
											seed.addEnsuredPredicate(new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
										 }
									 } else{
										Value param = ie.getArg(i-1);
										AccessGraph accessGraph = new AccessGraph((Local) param, param.getType());
										AnalysisSeedWithEnsuredPredicate seed = cryptoScanner
												.getOrCreateSeed(new FactAtStatement(currStmt, accessGraph));
										seed.addEnsuredPredicate(new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
									 }
								 }
								 i++;
							 }
						 }
					}
				}
			}
		}
	}

	private Analysis<TypestateDomainValue<StateNode>> getOrCreateAnalysis(
			ResultReporter<TypestateDomainValue<StateNode>> resultReporter) {
		if (analysis == null) {
			problem = new CryptoTypestateAnaylsisProblem() {
				@Override
				public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
					return resultReporter;
				}

				@Override
				public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
					return new FiniteStateMachineToTypestateChangeFunction(this);
				}

				@Override
				public IExtendedICFG icfg() {
					return cryptoScanner.icfg();
				}

				@Override
				public IDebugger<TypestateDomainValue<StateNode>> debugger() {
					return cryptoScanner.debugger();
				}

				@Override
				public StateMachineGraph getStateMachine() {
					return spec.getRule().getUsagePattern();
				}
			};
			analysis = new Analysis<TypestateDomainValue<StateNode>>(problem);
		}
		return analysis;
	}

	private boolean checkConstraintSystem() {
		ConstraintSolver solver = new ConstraintSolver(spec, parametersToValues);
		List<ISLConstraint> relConstraints = solver.getRelConstraints();
		if(!checkPredicates(relConstraints))
			return false;
		return 0 == solver.evaluateRelConstraints();
	}

	private boolean checkPredicates(List<ISLConstraint> relConstraints) {
		List<CryptSLPredicate> requiredPredicates = Lists.newLinkedList();		
		for (ISLConstraint con : relConstraints) {
			if (con instanceof CryptSLPredicate) {
				requiredPredicates.add((CryptSLPredicate) con);
			}
		}
		Set<CryptSLPredicate> remainingPredicates = Sets.newHashSet(requiredPredicates);
		for (CryptSLPredicate pred : requiredPredicates) {
			if(pred.isNegated()){
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if(ensPred.equals(pred))
						return false;
				}
				remainingPredicates.remove(pred);
			} else{
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if(ensPred.getPredicate().equals(pred)){
						//TODO Stefan: #13 Re-implement full predicate check
						remainingPredicates.remove(pred);
					}
				}
			}
		}
		return remainingPredicates.isEmpty();
	}

	private Multimap<String, String> convertToStringMultiMap(Multimap<CallSiteWithParamIndex, Value> actualValues) {
		Multimap<String, String> varVal = HashMultimap.create();
		for (CallSiteWithParamIndex callSite : actualValues.keySet()) {
			Collection<Value> collection = actualValues.get(callSite);
			List<String> values = new ArrayList<String>();
			for (Value val : collection) {
				if (val instanceof StringConstant) {
					StringConstant stringConstant = (StringConstant) val;
					values.add(stringConstant.value);
				} else {
					values.add(val.toString());
				}
			}
			varVal.putAll(callSite.getVarName(), values);
		}

		return varVal;
	}


	public Set<EnsuredCryptSLPredicate> getEnsuredPredicates() {
		return Collections.emptySet();
	}

	/**
	 * @return the parametersToValues
	 */
	public Multimap<String, String> getParametersToValues() {
		return parametersToValues;
	}

	@Override
	public AccessGraph getFact() {
		return factAtStmt.getFact();
	}

	@Override
	public Unit getStmt() {
		return factAtStmt.getStmt();
	}

	@Override
	public CryptoTypestateAnaylsisProblem getAnalysisProblem() {
		return problem;
	}

	@Override
	public boolean isSolved() {
		return solved;
	}

	public ClassSpecification getSpec() {
		return spec;
	}

	public void addEnsuredPredicate(EnsuredCryptSLPredicate ensPred) {
		if(ensuredPredicates.add(ensPred)){
			for(Entry<Unit, StateNode> e : typeStateChange.entries())
				onAddedTypestateChange(e.getKey(),e.getValue());
		}
	}
}
