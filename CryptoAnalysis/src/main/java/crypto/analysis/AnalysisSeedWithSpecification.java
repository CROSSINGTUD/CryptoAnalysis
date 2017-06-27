package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.ExtendedStandardFlowFunction;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import ideal.PerSeedAnalysisContext;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.StringConstant;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.Transition;

public class AnalysisSeedWithSpecification implements IAnalysisSeed{
	private final IFactAtStatement factAtStmt;
	private final SootMethod method;
	private final ClassSpecification spec;
	private final IAnalysisSeed parent;
	private CryptoScanner cryptoScanner;
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private Multimap<String, String> parametersToValues = HashMultimap.create();
	private Unit checkPredicateAtStmt;
	private CryptoTypestateAnaylsisProblem problem;
	private HashBasedTable<Unit, AccessGraph, TypestateDomainValue<StateNode>> results;
	private boolean solved;
	private Collection<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();

	
	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt, SootMethod method, ClassSpecification spec){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
		this.method = method;
		this.spec = spec;
		this.parent = null;
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
		return "AnalysisSeed [" + factAtStmt + " in "+method+" with spec " + spec.getRule().getClassName() + "]";
	}
	public void execute() {
		if(!checkPredicates()){
			return;
		}
		getOrCreateAnalysis(new ResultReporter<TypestateDomainValue<StateNode>>() {
			@Override
			public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
				parametersToValues = convertToStringMultiMap(problem.getCollectedValues());
				cryptoScanner.analysisListener().onSeedFinished(seed, solver);
				AnalysisSeedWithSpecification.this.onSeedFinished(seed, solver);
			}

			@Override
			public void onSeedTimeout(IFactAtStatement seed) {
			}
		}).analysisForSeed(this);
		
		//TODO Stefan: All method that are invoked on an object can be retrieved like this:
		problem.getInvokedMethodOnInstance();
		cryptoScanner.analysisListener().collectedValues(this, problem.getCollectedValues());
		solved = true;
		//TODO only execute when typestate and constraint solving did not fail.
//		ensuresPredicate();
	}

	private boolean checkPredicates() {
		List<CryptSLPredicate> requiredPredicates = spec.getRule().getRequiredPredicates();
		Set<CryptSLPredicate> remainingPredicates = Sets.newHashSet(requiredPredicates);
		for(CryptSLPredicate pred : requiredPredicates){
			for(EnsuredCryptSLPredicate ensPred : ensuredPredicates){
				if(ensPred.getPredicate().equals(pred)){
					remainingPredicates.remove(pred);
				}
					
			}
		}
		return remainingPredicates.isEmpty();
	}

	public void onSeedFinished(IFactAtStatement seed,
			AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		//Merge all information (all access graph here point to the seed object) 
		 results = solver.results();
		Multimap<Unit,StateNode> unitToStates = HashMultimap.create();
		for(Cell<Unit, AccessGraph, TypestateDomainValue<StateNode>> c : results.cellSet()){
			unitToStates.putAll(c.getRowKey(), c.getValue().getStates());
		}
		for(Unit curr : unitToStates.keySet()){
			Collection<StateNode> stateAtCurrMinusPred = Sets.newHashSet(unitToStates.get(curr));
			for(Unit pred : cryptoScanner.icfg().getPredsOf(curr)){
				Collection<StateNode> stateAtPred = unitToStates.get(pred);
				stateAtCurrMinusPred.removeAll(stateAtPred);
				for(StateNode newStateAtCurr : stateAtCurrMinusPred){
					typeStateChangeAtStatement(pred,newStateAtCurr);
				}
			}
		}
	}
	
	private void typeStateChangeAtStatement(Unit curr, StateNode stateNode) {
		final CryptSLRule rule = spec.getRule();
		for (CryptSLPredicate predToBeEnsured : rule.getPredicates()) {
			if(predToBeEnsured instanceof CryptSLCondPredicate && ((CryptSLCondPredicate) predToBeEnsured).getConditionalMethods().contains(stateNode) || stateNode.getAccepting()){
				ensuresPred(predToBeEnsured, curr,stateNode);
			}
		}
	}
	private void ensuresPred(CryptSLPredicate predToBeEnsured, Unit curStmt, StateNode stateNode) {
		this.checkPredicateAtStmt = curStmt;
		if (predToBeEnsured.isNegated()) {
//			for (EnsuredCryptSLPredicate ensPred : cryptoScanner.getExistingPredicates(curStmt, this)) {
//				if (ensPred.getPredicate().equals(predToBeEnsured)) {
//					cryptoScanner.deleteNewPred(curStmt, this, ensPred);
//				}
//			}
		} else {
			if (checkConstraintSystem()) {
				if(predToBeEnsured.getParameters().get(0).getName().equals("this")){
					for(Cell<Unit, AccessGraph, TypestateDomainValue<StateNode>> e : results.cellSet()){
						//TODO check for any reachable state that don't kill predicates.
						if(e.getValue().getStates().contains(stateNode)){
							cryptoScanner.addNewPred(e.getRowKey(), e.getColumnKey(), new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
						}
					}
				} else{
					if(curStmt instanceof AssignStmt){
						AssignStmt as = (AssignStmt) curStmt;
						Value leftOp = as.getLeftOp();
						AccessGraph accessGraph = new AccessGraph((Local)leftOp,leftOp.getType());
						for(Unit succ : cryptoScanner.icfg().getSuccsOf(as)){
							AnalysisSeedWithEnsuredPredicate seed = cryptoScanner.getOrCreateSeed(new FactAtStatement(succ,accessGraph));
							seed.addEnsuredPredicate(new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
						}
					}
				}
//				cryptoScanner.addNewPred(curStmt, this, new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
			}
		}
	}
	private Analysis<TypestateDomainValue<StateNode>> getOrCreateAnalysis(ResultReporter<TypestateDomainValue<StateNode>> resultReporter) {
		if(analysis == null){
			problem = new CryptoTypestateAnaylsisProblem() {
				@Override
				public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
					return resultReporter;
				}

				@Override
				public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
					return new FiniteStateMachineToTypestateChangeFunction(this){
	                    @Override
	                    public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite,
	                            AccessGraph d2, Unit returnSite, AccessGraph d3) {
	                        cryptoScanner.onCallToReturnFlow(AnalysisSeedWithSpecification.this, d1, callSite, d2);
	                        return super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
	                    }
					};
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

				@Override
				public StandardFlowFunctions<TypestateDomainValue<StateNode>> flowFunctions(
						PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context) {
					return new ExtendedStandardFlowFunction(context, spec.getRule());
				}

			};
			analysis = new Analysis<TypestateDomainValue<StateNode>>(problem);
		}
		return analysis;
	}
	private boolean checkConstraintSystem() {
		ConstraintSolver solver = new ConstraintSolver(spec.getRule(), parametersToValues);
		return 0 == solver.evaluateRelConstraints();
	}

	
	private Multimap<String, String> convertToStringMultiMap(Multimap<CallSiteWithParamIndex, Value> actualValues) {
		Multimap<String, String> varVal = HashMultimap.create();
		for (CallSiteWithParamIndex callSite : actualValues.keySet()) {
				Collection<Value> collection = actualValues.get(callSite);
				List<String> values = new ArrayList<String>();
				for (Value val: collection) {
					if(val instanceof StringConstant){
						StringConstant stringConstant = (StringConstant) val;
						values.add(stringConstant.value);
					}
					else{
						values.add(val.toString());
					}
				}
				varVal.putAll(callSite.getVarName(), values);
		}
			
		return varVal;
	}
	public ParentPredicate getParent(){
		return parent;
	}

	public Set<EnsuredCryptSLPredicate> getEnsuredPredicates(){
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
		ensuredPredicates.add(ensPred);
	}
}
