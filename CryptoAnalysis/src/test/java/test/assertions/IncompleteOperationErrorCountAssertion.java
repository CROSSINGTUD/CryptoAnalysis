package test.assertions;

public class IncompleteOperationErrorCountAssertion implements Assertion {

    private final int expectedErrorCounts;
    private int actualErrorCounts;

    public IncompleteOperationErrorCountAssertion(int numberOfCounts) {
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
                + " incomplete operation errors, but got "
                + actualErrorCounts;
    }
}
