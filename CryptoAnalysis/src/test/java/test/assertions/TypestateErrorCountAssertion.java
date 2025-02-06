package test.assertions;

import boomerang.scene.Val;
import java.util.Collection;
import test.Assertion;

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
                + " typestate errors on "
                + seed.getVariableName()
                + ", but got "
                + actualErrorCounts;
    }
}
