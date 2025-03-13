package test.assertions;

public interface Assertion {

    boolean isUnsound();

    boolean isImprecise();

    String getErrorMessage();
}
