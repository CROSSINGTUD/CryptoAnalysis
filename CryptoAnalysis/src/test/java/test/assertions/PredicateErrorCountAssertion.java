package test.assertions;

public class PredicateErrorCountAssertion implements Assertion {

    private final int expectedErrorCounts;
    private int actualErrorCounts;

    public PredicateErrorCountAssertion(int numberOfCounts) {
        this.expectedErrorCounts = numberOfCounts;
    }

    public void increaseCount() {
        actualErrorCounts++;
    }

    @Override
    public boolean isUnsound() {
        return expectedErrorCounts > actualErrorCounts;
    }

    @Override
    public boolean isImprecise() {
        return expectedErrorCounts < actualErrorCounts;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedErrorCounts
                + " predicate errors, but got "
                + actualErrorCounts;
    }
}
