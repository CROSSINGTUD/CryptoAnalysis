package test.assertions;

import boomerang.scene.Statement;
import test.Assertion;

public class CallToForbiddenMethodAssertion implements Assertion {

    private final Statement stmt;
    private boolean satisfied;

    public CallToForbiddenMethodAssertion(Statement stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isSatisfied() {
        return satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String toString() {
        return "Expected to report a call to a forbidden method at this statement: " + stmt;
    }

    public void reported(Statement callSite) {
        satisfied |= callSite.equals(stmt);
    }
}
