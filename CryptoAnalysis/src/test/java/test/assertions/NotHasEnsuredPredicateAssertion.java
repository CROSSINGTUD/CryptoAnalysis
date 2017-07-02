package test.assertions;

import boomerang.accessgraph.AccessGraph;
import crypto.analysis.EnsuredCryptSLPredicate;
import soot.jimple.Stmt;
import test.Assertion;

public class NotHasEnsuredPredicateAssertion implements Assertion {

	private Stmt stmt;
	private AccessGraph val;
	private boolean satisfied = true;

	public NotHasEnsuredPredicateAssertion(Stmt stmt, AccessGraph val) {
		this.stmt = stmt;
		this.val = val;
	}
	
	public AccessGraph getAccessGraph() {
		return val;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}


	public Stmt getStmt() {
		return stmt;
	}

	public void reported(AccessGraph seed, EnsuredCryptSLPredicate pred) {
		if(seed.equals(val))
			satisfied = false;
	}

	@Override
	public String toString() {
		return "Did not expecte a predicate for "+ val +" @ " + stmt;  
	}
}
