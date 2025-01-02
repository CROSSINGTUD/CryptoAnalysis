package test.assertions;

import boomerang.scene.Val;
import crypto.constraints.EvaluableConstraint;
import java.util.Collection;
import test.Assertion;

public class ConstraintsNotRelevantAssertion implements Assertion {

    private final Val val;
    private final int expectedConstraints;
    private int actualConstraints;

    public ConstraintsNotRelevantAssertion(Val val, int expectedConstraints) {
        this.val = val;
        this.expectedConstraints = expectedConstraints;

        actualConstraints = 0;
    }

    public void reported(Collection<Val> seed, EvaluableConstraint.EvaluationResult result) {
        if (seed.contains(val)
                && result == EvaluableConstraint.EvaluationResult.ConstraintIsNotRelevant) {
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
                + " irrelevant constraints on object "
                + val.getVariableName()
                + ", but got "
                + actualConstraints;
    }
}
