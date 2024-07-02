package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.HiddenPredicate;
import soot.jimple.Stmt;
import test.Assertion;

import java.util.Collection;

public class NotHasEnsuredPredicateAssertion implements Assertion {

	private final Statement stmt;
	private final Collection<Val> val;
	private final String predName;
	private boolean imprecise = false;

	public NotHasEnsuredPredicateAssertion(Statement stmt, Collection<Val> val) {
		this(stmt, val, null);
	}

	public NotHasEnsuredPredicateAssertion(Statement stmt, Collection<Val> val, String predName) {
		this.stmt = stmt;
		this.val = val;
		this.predName = predName;
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
		if (!val.contains(value) || pred instanceof HiddenPredicate) {
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
