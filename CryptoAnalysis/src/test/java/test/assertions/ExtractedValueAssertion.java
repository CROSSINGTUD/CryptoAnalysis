package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.extractparameter.CallSiteWithExtractedValue;
import java.util.Collection;
import test.Assertion;

public class ExtractedValueAssertion implements Assertion {

    private final Statement stmt;
    private final int index;
    private boolean satisfied;

    public ExtractedValueAssertion(Statement stmt, int index) {
        this.stmt = stmt;
        this.index = index;
    }

    public void computedValues(Collection<CallSiteWithExtractedValue> collectedValues) {
        for (CallSiteWithExtractedValue callSite : collectedValues) {
            Statement statement = callSite.callSiteWithParam().statement();

            if (callSite.extractedValue().val().equals(Val.zero())) {
                continue;
            }

            if (statement.equals(stmt) && callSite.callSiteWithParam().index() == index) {
                satisfied = true;
            }
        }
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
        return "Did not extract parameter with index: " + index + " @ " + stmt;
    }
}
