package test.assertions;

import test.Assertion;

public class NeverTypeOfErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public NeverTypeOfErrorCountAssertion(int numberOfCounts) {
        this.expectedErrorCount = numberOfCounts;
    }

    public void increaseCount() {
        actualErrorCount++;
    }

    @Override
    public boolean isSatisfied() {
        return expectedErrorCount <= actualErrorCount;
    }

    @Override
    public boolean isImprecise() {
        return expectedErrorCount != actualErrorCount;
    }

    @Override
    public String toString() {
        return "Expected "
                + expectedErrorCount
                + " neverTypeOf errors, but got "
                + actualErrorCount;
    }
}
