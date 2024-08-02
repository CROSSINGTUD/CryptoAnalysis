package test.assertions;

import test.Assertion;

public class TypestateErrorCountAssertion implements Assertion {

	private final int expectedErrorCounts;
	private int actualErrorCounts;

	public TypestateErrorCountAssertion(int numberOfCounts) {
		this.expectedErrorCounts = numberOfCounts;
	}
	
	public void increaseCount(){
		actualErrorCounts++;
	}

	@Override
	public boolean isSatisfied() {
		return expectedErrorCounts <= actualErrorCounts;
	}

	@Override
	public boolean isImprecise() {
		return expectedErrorCounts != actualErrorCounts;
	}

	@Override
	public String toString() {
		return "Expected " + expectedErrorCounts + " typestate errors, but got " + actualErrorCounts;
	}

}
