package test.assertions;

import test.Assertion;

public class ForbiddenMethodErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public ForbiddenMethodErrorCountAssertion(int expectedErrorCount) {
        this.expectedErrorCount = expectedErrorCount;
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
        return "Expected " + expectedErrorCount + " forbidden method errors, but got " + actualErrorCount;
    }
}
