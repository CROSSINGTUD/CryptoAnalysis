package test.assertions;

import test.Assertion;

public class TypestateErrorCountAssertion implements Assertion {

	private int expectedErrorCounts;
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

}
