package test.assertions;

import boomerang.scene.Statement;
import test.Assertion;

public class ConstraintViolationAssertion implements Assertion {
	
	private Statement stmt;
	private boolean satisfied;

	public ConstraintViolationAssertion(Statement stmt) {
		this.stmt = stmt;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	@Override
	public String toString() {
		return "Expected to report that a constraint is broken at this statement: " + this.stmt;
	}

	public void reported(Statement callSite) {
		satisfied |= callSite.equals(stmt);
	}

}
