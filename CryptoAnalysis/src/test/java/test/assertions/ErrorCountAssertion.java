package test.assertions;

import test.Assertion;

public class ErrorCountAssertion implements Assertion {

	private int expectedCount;
	private int actualCount = 0;
	
	public ErrorCountAssertion(int expectedCount) {
		this.expectedCount = expectedCount;
	}

	@Override
	public boolean isSatisfied() {
		return expectedCount == actualCount;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	@Override
	public String toString() {
		return "Expected " + expectedCount + " Errors, but found " + actualCount + ".";
	}

	public void increase() {
		actualCount++;
	}
}
