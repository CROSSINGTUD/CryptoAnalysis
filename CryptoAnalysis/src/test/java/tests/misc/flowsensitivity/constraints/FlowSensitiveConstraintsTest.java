/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.misc.flowsensitivity.constraints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.FLOW_SENSITIVITY_CONSTRAINTS)
public class FlowSensitiveConstraintsTest {

    private boolean staticallyUnknown() {
        return Math.random() > 0.5;
    }

    @Test
    public void positiveSimpleConstraintsTest() {
        FlowSensitiveConstraint constraint = new FlowSensitiveConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        constraint.operation1();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        constraint.operation2(10);
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 0);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void negativeSimpleConstraintsTest() {
        FlowSensitiveConstraint constraint = new FlowSensitiveConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        constraint.operation1();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        // At this point, we violate the constraint; hence, the object is not secure AFTER this
        // statement
        constraint.operation2(100);
        Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 1);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void satisfiedToViolatedConstraint() {
        OverriddenConstraint constraint = new OverriddenConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        // satisfied
        constraint.operation(10);
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        // violated
        constraint.operation(100);
        Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 1);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void violatedToSatisfiedConstraint() {
        OverriddenConstraint constraint = new OverriddenConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        // violated
        constraint.operation(100);
        Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");

        // satisfied
        constraint.operation(10);
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 1);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void satisfiedToViolatedBranchedConstraint() {
        OverriddenConstraint constraint = new OverriddenConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        if (staticallyUnknown()) {
            // satisfied
            constraint.operation(10);
            Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");
        }

        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        if (staticallyUnknown()) {
            // violated
            constraint.operation(100);
            Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");
        }

        // Violated because there is a sequence to the violating call
        Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 1);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void violatedToSatisfiedBranchedConstraint() {
        OverriddenConstraint constraint = new OverriddenConstraint();
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        if (staticallyUnknown()) {
            // violated
            constraint.operation(100);
            Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");
        }

        Assertions.notHasEnsuredPredicate(constraint, "generatedConstraint");

        // satisfied
        constraint.operation(10);
        Assertions.hasEnsuredPredicate(constraint, "generatedConstraint");

        Assertions.constraintErrors(constraint, 1);
        Assertions.typestateErrors(constraint, 0);
        Assertions.incompleteOperationErrors(0);
    }
}
