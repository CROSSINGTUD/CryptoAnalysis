package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.HiddenPredicate;
import test.Assertion;

import java.util.Collection;

public class HasEnsuredPredicateAssertion implements Assertion {

	private final Statement stmt;
	private final Collection<Val> val;
	private final String predName;
	private boolean satisfied;

	public HasEnsuredPredicateAssertion(Statement stmt, Collection<Val> val) {
		this(stmt, val, null);
	}

	public HasEnsuredPredicateAssertion(Statement stmt, Collection<Val> val, String predName) {
		this.stmt = stmt;
		this.val = val;
		this.predName = predName;
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
		if (!val.contains(seed) || pred instanceof HiddenPredicate) {
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
