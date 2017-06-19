package test.assertions;

import soot.Unit;
import test.Assertion;

public class ConstraintViolationAssertion implements Assertion {
	
	private Unit stmt;
	private boolean satisfied;

	public ConstraintViolationAssertion(Unit stmt) {
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

	public void reported(Unit callSite) {
		satisfied |= callSite.equals(stmt);
	}

}
