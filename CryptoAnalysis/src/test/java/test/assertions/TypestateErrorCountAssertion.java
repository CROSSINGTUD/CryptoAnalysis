package test.assertions;

import boomerang.scope.Val;
import java.util.Collection;

public class TypestateErrorCountAssertion implements Assertion {

    private final Val seed;
    private final int expectedErrorCounts;
    private int actualErrorCounts;

    public TypestateErrorCountAssertion(Val seed, int numberOfCounts) {
        this.seed = seed;
        this.expectedErrorCounts = numberOfCounts;
    }

    public void increaseCount(Collection<Val> vals) {
        if (vals.contains(seed)) {
            actualErrorCounts++;
        }
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
                + " typestate errors on "
                + seed.getVariableName()
                + ", but got "
                + actualErrorCounts;
    }
}
