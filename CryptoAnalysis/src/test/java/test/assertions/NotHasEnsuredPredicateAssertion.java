package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.HiddenPredicate;
import soot.jimple.Stmt;
import test.Assertion;

public class NotHasEnsuredPredicateAssertion implements Assertion {

	private Statement stmt;
	private Val val;
	private boolean imprecise = false;
	private String predName;

	public NotHasEnsuredPredicateAssertion(Statement stmt, Val val) {
		this(stmt, val, null);
	}

	public NotHasEnsuredPredicateAssertion(Statement stmt, Val val, String predName) {
		this.stmt = stmt;
		this.val = val;
		this.predName = predName;
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


	public Statement getStmt() {
		return stmt;
	}

	public void reported(Val value, EnsuredCrySLPredicate pred) {
		if (!value.equals(val) || pred instanceof HiddenPredicate) {
			return;
		}

		if (predName == null || pred.getPredicate().getPredName().equals(predName)) {
			imprecise = true;
		}
	}

	@Override
	public String toString() {
		if (predName == null) {
			return "Did not expect a predicate for " + val + " @ " + stmt;
		} else {
			return "Did not expect '" + predName + "' ensured on " + val + " @ " + stmt;
		}
	}
}
