package test.assertions;

import boomerang.scope.Val;
import crypto.constraints.EvaluableConstraint;
import java.util.Collection;

public class ConstraintsViolatedAssertion implements Assertion {

    private final Val val;
    private final int expectedConstraints;
    private int actualConstraints;

    public ConstraintsViolatedAssertion(Val val, int expectedConstraints) {
        this.val = val;
        this.expectedConstraints = expectedConstraints;

        actualConstraints = 0;
    }

    public void reported(Collection<Val> seed, EvaluableConstraint.EvaluationResult result) {
        if (seed.contains(val)
                && result == EvaluableConstraint.EvaluationResult.ConstraintIsNotSatisfied) {
            actualConstraints++;
        }
    }

    @Override
    public boolean isUnsound() {
        return expectedConstraints > actualConstraints;
    }

    @Override
    public boolean isImprecise() {
        return expectedConstraints < actualConstraints;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedConstraints
                + " violated constraints on object "
                + val.getVariableName()
                + ", but got "
                + actualConstraints;
    }
}
