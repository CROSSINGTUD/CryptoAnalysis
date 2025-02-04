package test.assertions;

import boomerang.scene.Val;
import java.util.Collection;
import test.Assertion;

public class ConstraintsEvaluatedAssertion implements Assertion {

    private final Val val;
    private final int expectedConstraints;
    private int actualConstraints;

    public ConstraintsEvaluatedAssertion(Val val, int expectedConstraints) {
        this.val = val;
        this.expectedConstraints = expectedConstraints;

        actualConstraints = 0;
    }

    public void reported(Collection<Val> seed) {
        if (seed.contains(val)) {
            actualConstraints++;
        }
    }

    @Override
    public boolean isSatisfied() {
        return expectedConstraints == actualConstraints;
    }

    @Override
    public boolean isImprecise() {
        return expectedConstraints != actualConstraints;
    }

    @Override
    public String toString() {
        return "Expected "
                + expectedConstraints
                + " evaluated constraints on object "
                + val.getVariableName()
                + ", but got "
                + actualConstraints;
    }
}
