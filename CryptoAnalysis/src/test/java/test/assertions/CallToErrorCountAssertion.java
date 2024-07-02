package test.assertions;

import test.Assertion;

public class CallToErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public CallToErrorCountAssertion(int numberOfCounts) {
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
        return "Expected " + expectedErrorCount + " callTo errors, but got " + actualErrorCount;
    }
}
