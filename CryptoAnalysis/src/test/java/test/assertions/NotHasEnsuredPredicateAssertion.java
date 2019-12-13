package test.assertions;

import boomerang.jimple.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import soot.jimple.Stmt;
import test.Assertion;

public class NotHasEnsuredPredicateAssertion implements Assertion {

	private Stmt stmt;
	private Val val;
	private boolean imprecise = false;

	public NotHasEnsuredPredicateAssertion(Stmt stmt, Val val) {
		this.stmt = stmt;
		this.val = val;
	}
	
	public Val getAccessGraph() {
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

	public void reported(Val value, EnsuredCrySLPredicate pred) {
		if(value.equals(val)){
			imprecise = true;
		}
	}

	@Override
	public String toString() {
		return "Did not expect a predicate for "+ val +" @ " + stmt;  
	}
}
