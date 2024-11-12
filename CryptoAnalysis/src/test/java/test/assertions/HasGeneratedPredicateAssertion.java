package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.HiddenPredicate;
import test.Assertion;

import java.util.Collection;

public class HasGeneratedPredicateAssertion implements Assertion {

	private final Statement statement;
	private final Val val;
	private boolean satisfied = false;

	public HasGeneratedPredicateAssertion(Statement statement, Val val) {
		this.statement = statement;
		this.val = val;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	public void reported(Collection<Val> seed, EnsuredCrySLPredicate predicate) {
		if (seed.contains(val) && !(predicate instanceof HiddenPredicate)) {
			satisfied = true;
		}
	}

	public Statement getStatement() {
		return statement;
	}

	@Override
	public String toString() {
		return "Expected " + val.getVariableName() + " to generate a predicate @ " + statement;
	}
}
