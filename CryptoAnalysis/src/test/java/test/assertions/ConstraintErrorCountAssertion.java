package test.assertions;

import test.Assertion;

public class ConstraintErrorCountAssertion implements Assertion {

    private final int expectedErrorCounts;
    private int actualErrorCounts;

    public ConstraintErrorCountAssertion(int numberOfCounts) {
        this.expectedErrorCounts = numberOfCounts;
    }

    public void increaseCount() {
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
        return "Expected "
                + expectedErrorCounts
                + " constraint errors, but got "
                + actualErrorCounts;
    }
}
