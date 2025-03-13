package test.assertions;

import boomerang.scope.Statement;

public class CallToForbiddenMethodAssertion implements Assertion {

    private final Statement stmt;
    private boolean satisfied;

    public CallToForbiddenMethodAssertion(Statement stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isUnsound() {
        return !satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "Expected to report a call to a forbidden method at this statement: " + stmt;
    }

    public void reported(Statement callSite) {
        satisfied |= callSite.equals(stmt);
    }
}
