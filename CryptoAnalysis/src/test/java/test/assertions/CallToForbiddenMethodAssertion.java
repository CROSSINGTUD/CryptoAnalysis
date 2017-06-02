package test.assertions;

import soot.Unit;
import test.Assertion;

public class CallToForbiddenMethodAssertion implements Assertion {

	private Unit stmt;
	private boolean satisfied;

	public CallToForbiddenMethodAssertion(Unit stmt) {
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
		return "Expected to report a call to a forbidden method at this statement: " + stmt;
	}

	public void reported(Unit callSite) {
		satisfied |= callSite.equals(stmt);
	}
}
