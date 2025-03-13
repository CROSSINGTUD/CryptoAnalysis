package test.assertions;

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
                + " imprecise value extraction errors, but got "
                + actualErrorCount;
    }
}
