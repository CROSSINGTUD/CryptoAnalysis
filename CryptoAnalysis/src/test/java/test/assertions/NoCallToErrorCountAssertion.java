package test.assertions;

import test.Assertion;

public class NoCallToErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public NoCallToErrorCountAssertion(int numberOfCounts) {
        this.expectedErrorCount = numberOfCounts;
    }

    public void increaseCount(){
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
        return "Expected " + expectedErrorCount + " noCallTo errors, but got " + actualErrorCount;
    }
}
