package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class AnalysisSeedWithSpecification implements IFactAtStatement, ParentPredicate{
	private final IFactAtStatement factAtStmt;
	private final SootMethod method;
	private final ClassSpecification spec;
	private final AnalysisSeedWithSpecification parent;
	private CryptoScanner cryptoScanner;
	private List<EnsuredCryptSLPredicate> ensuredPredicates = Lists.newLinkedList();
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private Multimap<String, String> parametersToValues = HashMultimap.create();

	
	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt, SootMethod method, ClassSpecification spec){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
		this.method = method;
		this.spec = spec;
		this.parent = null;
	}
	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt,SootMethod method, ClassSpecification spec, AnalysisSeedWithSpecification parent){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
		this.method = method;
		this.spec = spec;
		this.parent = parent;
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
		getOrCreateAnalysis(new ResultReporter<TypestateDomainValue<StateNode>>() {

			@Override
			public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
				cryptoScanner.analysisListener().onSeedFinished(seed, solver);
				AnalysisSeedWithSpecification.this.onSeedFinished(seed, solver);
			}

			@Override
			public void onSeedTimeout(IFactAtStatement seed) {
			}
		}).analysisForSeed(this);
		
		//TODO Stefan: All method that are invoked on an object can be retrieved like this:
		spec.getAnalysisProblem().getInvokedMethodOnInstance();
		cryptoScanner.analysisListener().collectedValues(this, spec.getAnalysisProblem().getCollectedValues());
		parametersToValues = convertToStringMultiMap(spec.getAnalysisProblem().getCollectedValues());
		checkConstraintSystem();
		//TODO only execute when typestate and constraint solving did not fail.
//		ensuresPredicate();
	}

	public void onSeedFinished(IFactAtStatement seed,
			AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		for(Cell<Unit,AccessGraph, TypestateDomainValue<StateNode>>c : solver.results().cellSet()){
			Unit curStmt = c.getRowKey();
			List<EnsuredCryptSLPredicate> curEnsPreds = new ArrayList<EnsuredCryptSLPredicate>();
			for(StateNode stateNode : c.getValue().getStates()){
				final CryptSLRule rule = spec.getRule();
				for (CryptSLPredicate predToBeEnsured : rule.getPredicates()) {
					if(predToBeEnsured instanceof CryptSLCondPredicate){
						CryptSLCondPredicate cryptSLCondPredicate = (CryptSLCondPredicate) predToBeEnsured;
						if (cryptSLCondPredicate.getConditionalMethods().contains(stateNode)) {
							//// add pred
							ensuresPred(predToBeEnsured, curStmt, rule.getConstraints());
						}
					}else{

						if (stateNode.getAccepting()) {
							//// add pred
							ensuresPred(predToBeEnsured, curStmt, rule.getConstraints());
						}
					}
				}
			}
		}
	}
	
	private void ensuresPred(CryptSLPredicate predToBeEnsured, Unit curStmt, List<ISLConstraint> constraints) {
		if (predToBeEnsured.isNegated()) {
			for (EnsuredCryptSLPredicate ensPred : cryptoScanner.getExistingPredicates(curStmt, this)) {
				if (ensPred.getPredicate().equals(predToBeEnsured)) {
					cryptoScanner.deleteNewPred(curStmt, this, ensPred);
				}
			}
		} else {
			if (checkConstraintSystem()) {
				cryptoScanner.addNewPred(curStmt, this, new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
			}
		}
	}
	private Analysis<TypestateDomainValue<StateNode>> getOrCreateAnalysis(ResultReporter<TypestateDomainValue<StateNode>> resultReporter) {
		if(analysis == null)
			analysis = spec.createTypestateAnalysis(resultReporter);
		return analysis;
	}
	private boolean checkConstraintSystem() {
		Multimap<CallSiteWithParamIndex, Value> actualValues = spec.getAnalysisProblem().getCollectedValues();
		Multimap<String, String> stringValues = convertToStringMultiMap(actualValues);
		ConstraintSolver solver = new ConstraintSolver(this.parent, spec.getRule(), stringValues);
		return 0 == solver.evaluateRelConstraints();
	}

	
	private Multimap<String, String> convertToStringMultiMap(Multimap<CallSiteWithParamIndex, Value> actualValues) {
		Multimap<String, String> varVal = HashMultimap.create();
		for (CallSiteWithParamIndex callSite : actualValues.keySet()) {
				Collection<Value> collection = actualValues.get(callSite);
				List<String> values = new ArrayList<String>();
				for (Value val: collection) {
					values.add(val.toString());
				}
				varVal.putAll(callSite.getVarName(), values);
		}
			
		return null;
	}
	public ParentPredicate getParent(){
		return parent;
	}

	public List<EnsuredCryptSLPredicate> getEnsuredPredicates(){
		return Lists.newArrayList(ensuredPredicates);
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
}
