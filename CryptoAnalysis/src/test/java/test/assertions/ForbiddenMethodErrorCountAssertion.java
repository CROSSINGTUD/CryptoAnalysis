package test.assertions;

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
    public boolean isUnsound() {
        return expectedErrorCount > actualErrorCount;
    }

    @Override
    public boolean isImprecise() {
        return expectedErrorCount < actualErrorCount;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedErrorCount
                + " forbidden method errors, but got "
                + actualErrorCount;
    }
}
