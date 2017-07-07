package test.assertions;

import boomerang.accessgraph.AccessGraph;
import crypto.analysis.EnsuredCryptSLPredicate;
import soot.jimple.Stmt;
import test.Assertion;

public class NotHasEnsuredPredicateAssertion implements Assertion {

	private Stmt stmt;
	private AccessGraph val;
	private boolean imprecise = false;

	public NotHasEnsuredPredicateAssertion(Stmt stmt, AccessGraph val) {
		this.stmt = stmt;
		this.val = val;
	}
	
	public AccessGraph getAccessGraph() {
		return val;
	}

	@Override
	public boolean isSatisfied() {
		return true;
	}

	@Override
	public boolean isImprecise() {
		return imprecise;
	}


	public Stmt getStmt() {
		return stmt;
	}

	public void reported(AccessGraph seed, EnsuredCryptSLPredicate pred) {
		if(seed.equals(val)){
			imprecise = true;
		}
	}

	@Override
	public String toString() {
		return "Did not expect a predicate for "+ val +" @ " + stmt;  
	}
}
