package test.assertions;

import test.Assertion;

public class ImpreciseValueExtractionErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public ImpreciseValueExtractionErrorCountAssertion(int expectedErrorCount) {
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
        return "Expected " + expectedErrorCount + " imprecise value extraction errors, but got " + actualErrorCount;
    }
}
