package test.assertions;

import boomerang.jimple.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import soot.jimple.Stmt;
import test.Assertion;

public class HasEnsuredPredicateAssertion implements Assertion {

	private Stmt stmt;
	private Val val;
	private boolean satisfied;

	public HasEnsuredPredicateAssertion(Stmt stmt,  Val val) {
		this.stmt = stmt;
		this.val = val;
	}
	
	public Val getAccessGraph() {
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

	public void reported(Val seed, EnsuredCrySLPredicate pred) {
		if(seed.equals(val))
			satisfied = true;
	}

	@Override
	public String toString() {
		return "Expected a predicate for "+ val +" @ " + stmt;  
	}
}
