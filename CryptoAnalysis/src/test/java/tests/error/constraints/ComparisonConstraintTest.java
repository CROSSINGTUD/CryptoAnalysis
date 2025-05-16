/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.constraints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.CONSTRAINTS)
public class ComparisonConstraintTest {

    @Test
    public void testPositiveEqualConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // equal1 == equal2
        constraint.equal(false, false);
        constraint.equal(true, true);

        // equalConstant == true
        constraint.equalConstant(true);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testNegativeEqualConstraint() {
        // equal1 == equal2
        ComparisonConstraint constraint1 = new ComparisonConstraint();
        constraint1.equal(false, true);

        Assertions.evaluatedConstraints(constraint1, 12);
        Assertions.satisfiedConstraints(constraint1, 0);
        Assertions.violatedConstraints(constraint1, 1);
        Assertions.notRelevantConstraints(constraint1, 11);
        Assertions.constraintErrors(constraint1, 1);

        // equalConstant == true
        ComparisonConstraint constraint2 = new ComparisonConstraint();
        constraint2.equalConstant(false);

        Assertions.evaluatedConstraints(constraint2, 12);
        Assertions.satisfiedConstraints(constraint2, 0);
        Assertions.violatedConstraints(constraint2, 1);
        Assertions.notRelevantConstraints(constraint2, 11);
        Assertions.constraintErrors(constraint2, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testPositiveUnEqualConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // equal1 != equal2
        constraint.unequal(true, false);
        constraint.unequal(true, false);

        // equalConstant != false
        constraint.unequalConstant(true);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testNegativeUnEqualConstraint() {
        // equal1 != equal2
        ComparisonConstraint constraint1 = new ComparisonConstraint();
        constraint1.unequal(false, false);

        Assertions.evaluatedConstraints(constraint1, 12);
        Assertions.satisfiedConstraints(constraint1, 0);
        Assertions.violatedConstraints(constraint1, 1);
        Assertions.notRelevantConstraints(constraint1, 11);
        Assertions.constraintErrors(constraint1, 1);

        // equalConstant != false
        ComparisonConstraint constraint2 = new ComparisonConstraint();
        constraint2.unequalConstant(false);

        Assertions.evaluatedConstraints(constraint2, 12);
        Assertions.satisfiedConstraints(constraint2, 0);
        Assertions.violatedConstraints(constraint2, 1);
        Assertions.notRelevantConstraints(constraint2, 11);
        Assertions.constraintErrors(constraint2, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testPositiveGreaterConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // greater1 > greater2
        constraint.greater(20, 10);

        // greaterConstant > 10
        constraint.greaterConstant(20);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testNegativeGreaterConstraint() {
        // greater1 > greater2
        ComparisonConstraint constraint1 = new ComparisonConstraint();
        constraint1.greater(10, 20);

        Assertions.evaluatedConstraints(constraint1, 12);
        Assertions.satisfiedConstraints(constraint1, 0);
        Assertions.violatedConstraints(constraint1, 1);
        Assertions.notRelevantConstraints(constraint1, 11);
        Assertions.constraintErrors(constraint1, 1);

        // greaterConstant > 10
        ComparisonConstraint constraint2 = new ComparisonConstraint();
        constraint2.greaterConstant(5);

        Assertions.evaluatedConstraints(constraint2, 12);
        Assertions.satisfiedConstraints(constraint2, 0);
        Assertions.violatedConstraints(constraint2, 1);
        Assertions.notRelevantConstraints(constraint2, 11);
        Assertions.constraintErrors(constraint2, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testPositiveGreaterEqualConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // greaterEqual1 >= greaterEqual2 + 10
        constraint.greaterEqual(20, 10);

        // greaterEqualConstant1 % greaterEqualConstant2 >= 5
        constraint.greaterEqualConstant(36, 10);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testNegativeGreaterEqualConstraint() {
        // greaterEqual1 >= greaterEqual2 + 10
        ComparisonConstraint constraint1 = new ComparisonConstraint();
        constraint1.greaterEqualConstant(10, 10);

        Assertions.evaluatedConstraints(constraint1, 12);
        Assertions.satisfiedConstraints(constraint1, 0);
        Assertions.violatedConstraints(constraint1, 1);
        Assertions.notRelevantConstraints(constraint1, 11);
        Assertions.constraintErrors(constraint1, 1);

        // greaterEqualConstant1 % greaterEqualConstant2 >= 5
        ComparisonConstraint constraint2 = new ComparisonConstraint();
        constraint2.greaterEqualConstant(34, 10);

        Assertions.evaluatedConstraints(constraint2, 12);
        Assertions.satisfiedConstraints(constraint2, 0);
        Assertions.violatedConstraints(constraint2, 1);
        Assertions.notRelevantConstraints(constraint2, 11);
        Assertions.constraintErrors(constraint2, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testPositiveLessConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // less1 - 10 < less2 + less3
        constraint.less(20, 15, 15);

        // 10 < lessConstant
        constraint.lessConstant(30);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testNegativeLessConstraint() {
        // less1 - 10 < less2 + less3
        ComparisonConstraint constraint1 = new ComparisonConstraint();
        constraint1.less(100, 30, 40);

        Assertions.evaluatedConstraints(constraint1, 12);
        Assertions.satisfiedConstraints(constraint1, 0);
        Assertions.violatedConstraints(constraint1, 1);
        Assertions.notRelevantConstraints(constraint1, 11);
        Assertions.constraintErrors(constraint1, 1);

        // 10 < lessConstant;
        ComparisonConstraint constraint2 = new ComparisonConstraint();
        constraint2.lessConstant(5);

        Assertions.evaluatedConstraints(constraint2, 12);
        Assertions.satisfiedConstraints(constraint2, 0);
        Assertions.violatedConstraints(constraint2, 1);
        Assertions.notRelevantConstraints(constraint2, 11);
        Assertions.constraintErrors(constraint2, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testPositiveLessEqualConstraint() {
        ComparisonConstraint constraint = new ComparisonConstraint();

        // lessEqual1 + 15 - lessEqual2 <= lessEqual3
        constraint.lessEqual(20, 15, 30);

        // lessEqualConstant <= 15
        constraint.lessEqualConstant(30);

        Assertions.evaluatedConstraints(constraint, 12);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 10);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }
}
