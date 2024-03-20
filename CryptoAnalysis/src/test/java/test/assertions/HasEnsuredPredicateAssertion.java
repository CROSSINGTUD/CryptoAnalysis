package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.HiddenPredicate;
import test.Assertion;

public class HasEnsuredPredicateAssertion implements Assertion {

	private Statement stmt;
	private Val val;
	private boolean satisfied;
	private String predName;

	public HasEnsuredPredicateAssertion(Statement stmt, Val val) {
		this(stmt, val, null);
	}

	public HasEnsuredPredicateAssertion(Statement stmt, Val val, String predName) {
		this.stmt = stmt;
		this.val = val;
		this.predName = predName;
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


	public Statement getStmt() {
		return stmt;
	}

	public void reported(Val seed, EnsuredCrySLPredicate pred) {
		if (!seed.equals(val) || pred instanceof HiddenPredicate) {
			return;
		}

		if (predName == null || pred.getPredicate().getPredName().equals(predName)) {
			satisfied = true;
		}
	}

	@Override
	public String toString() {
		if (predName == null) {
			return "Expected a predicate for "+ val +" @ " + stmt;
		} else {
			return "Expected '" + predName + "' ensured on " + val + " @ " + stmt;
		}
	}
}
