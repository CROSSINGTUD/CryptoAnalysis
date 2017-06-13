package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.Analysis;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class AnalysisSeedWithSpecification implements IFactAtStatement, ParentPredicate{
	private final IFactAtStatement factAtStmt;
	private final ClassSpecification spec;
	private final AnalysisSeedWithSpecification parent;
	private CryptoScanner cryptoScanner;
	private List<EnsuredCryptSLPredicate> ensuredPredicates = Lists.newLinkedList();
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt, ClassSpecification spec){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
		this.spec = spec;
		this.parent = null;
	}
	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt, ClassSpecification spec, AnalysisSeedWithSpecification parent){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
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
		return "AnalysisSeedWithSpecification [factAtStmt=" + factAtStmt + ", spec=" + spec + "]";
	}
	public void execute() {
		getOrCreateAnalysis().analysisForSeed(this);
		cryptoScanner.analysisListener().collectedValues(this, spec.getAnalysisProblem().getCollectedValues());
		checkConstraintSystem();
		//TODO only execute when typestate and constraint solving did not fail.
		ensuresPredicate();
	}

	private Analysis<TypestateDomainValue<StateNode>> getOrCreateAnalysis() {
		if(analysis == null)
			analysis = spec.createTypestateAnalysis();
		return analysis;
	}
	private void ensuresPredicate() {
		//TODO match to appropriate predicates. 
		Multimap<CallSiteWithParamIndex, Value> collectedValues = spec.getAnalysisProblem().getCollectedValues();
		Multimap<String, String> parametersToValues = HashMultimap.create();
		for(CryptSLPredicate predicate : spec.getRule().getPredicates()){
			for(Entry<CallSiteWithParamIndex, Value> e : collectedValues.entries()){
				if(predicate.getParameters().contains(e.getKey().getVarName())){
					parametersToValues.put(e.getKey().getVarName(), e.getValue().toString());
				}
			}
			ensuredPredicates.add(new EnsuredCryptSLPredicate(predicate, parametersToValues));
		}
	}
	private void checkConstraintSystem() {
		Multimap<CallSiteWithParamIndex, Value> actualValues = spec.getAnalysisProblem().getCollectedValues();
		Multimap<String, String> stringValues = convertToStringMultiMap(actualValues);
		ConstraintSolver solver = new ConstraintSolver(this.parent);
		for (ISLConstraint cons : spec.getRule().getConstraints()) {
			if (!solver.evaluate(cons, stringValues)) {
				// report error
			}
		}
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

	public LinkedList<EnsuredCryptSLPredicate> getEnsuredPredicates(){
		return Lists.newLinkedList(ensuredPredicates);
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
