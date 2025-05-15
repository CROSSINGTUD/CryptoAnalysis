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
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class BinaryConstraintTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "constraints";
    }

    @Test
    public void testPositiveImplication() {
        // implication1 in {"A", "B"} => implication2 in {10, 20}
        // implicationSame1 in {"A"} => implicationSame2 == 10

        // true => true
        BinaryConstraint trueImpliesTrue = new BinaryConstraint();
        trueImpliesTrue.implication1("A");
        trueImpliesTrue.implication2(10);
        trueImpliesTrue.implication("A", 10);

        Assertions.evaluatedConstraints(trueImpliesTrue, 6);
        Assertions.satisfiedConstraints(trueImpliesTrue, 2);
        Assertions.violatedConstraints(trueImpliesTrue, 0);
        Assertions.notRelevantConstraints(trueImpliesTrue, 4);
        Assertions.constraintErrors(trueImpliesTrue, 0);

        // false => false
        BinaryConstraint falseImpliesFalse = new BinaryConstraint();
        falseImpliesFalse.implication1("A");
        falseImpliesFalse.implication2(10);
        falseImpliesFalse.implication("A", 10);

        Assertions.evaluatedConstraints(falseImpliesFalse, 6);
        Assertions.satisfiedConstraints(falseImpliesFalse, 2);
        Assertions.violatedConstraints(falseImpliesFalse, 0);
        Assertions.notRelevantConstraints(falseImpliesFalse, 4);
        Assertions.constraintErrors(falseImpliesFalse, 0);

        // false => true
        BinaryConstraint falseImpliesTrue = new BinaryConstraint();
        falseImpliesTrue.implication1("C");
        falseImpliesTrue.implication2(10);
        falseImpliesTrue.implication("C", 10);

        Assertions.evaluatedConstraints(falseImpliesTrue, 6);
        Assertions.satisfiedConstraints(falseImpliesTrue, 2);
        Assertions.violatedConstraints(falseImpliesTrue, 0);
        Assertions.notRelevantConstraints(falseImpliesTrue, 4);
        Assertions.constraintErrors(falseImpliesTrue, 0);
    }

    @Test
    public void testNotRelevantImplication() {
        // implication1 in {"A", "B"} => implication2 in {10, 20}
        // implicationSame1 in {"A"} => implicationSame2 == 10

        // true => not relevant
        BinaryConstraint trueImpliesNotRelevant = new BinaryConstraint();
        trueImpliesNotRelevant.implication1("A");

        Assertions.evaluatedConstraints(trueImpliesNotRelevant, 6);
        Assertions.satisfiedConstraints(trueImpliesNotRelevant, 0);
        Assertions.violatedConstraints(trueImpliesNotRelevant, 0);
        Assertions.notRelevantConstraints(trueImpliesNotRelevant, 6);
        Assertions.constraintErrors(trueImpliesNotRelevant, 0);

        // false => not relevant
        BinaryConstraint falseImpliesNotRelevant = new BinaryConstraint();
        falseImpliesNotRelevant.implication1("X");

        Assertions.evaluatedConstraints(falseImpliesNotRelevant, 6);
        Assertions.satisfiedConstraints(falseImpliesNotRelevant, 0);
        Assertions.violatedConstraints(falseImpliesNotRelevant, 0);
        Assertions.notRelevantConstraints(falseImpliesNotRelevant, 6);
        Assertions.constraintErrors(falseImpliesNotRelevant, 0);

        // not relevant => true
        BinaryConstraint notRelevantImpliesTrue = new BinaryConstraint();
        notRelevantImpliesTrue.implication2(10);

        Assertions.evaluatedConstraints(notRelevantImpliesTrue, 6);
        Assertions.satisfiedConstraints(notRelevantImpliesTrue, 1);
        Assertions.violatedConstraints(notRelevantImpliesTrue, 0);
        Assertions.notRelevantConstraints(notRelevantImpliesTrue, 5);
        Assertions.constraintErrors(notRelevantImpliesTrue, 0);

        // not relevant => false
        BinaryConstraint notRelevantImpliesFalse = new BinaryConstraint();
        notRelevantImpliesFalse.implication2(100);

        Assertions.evaluatedConstraints(notRelevantImpliesFalse, 6);
        Assertions.satisfiedConstraints(notRelevantImpliesFalse, 1);
        Assertions.violatedConstraints(notRelevantImpliesFalse, 0);
        Assertions.notRelevantConstraints(notRelevantImpliesFalse, 5);
        Assertions.constraintErrors(notRelevantImpliesFalse, 0);

        // not relevant => false
        BinaryConstraint notRelevantImpliesNotRelevant = new BinaryConstraint();

        Assertions.evaluatedConstraints(notRelevantImpliesNotRelevant, 6);
        Assertions.satisfiedConstraints(notRelevantImpliesNotRelevant, 0);
        Assertions.violatedConstraints(notRelevantImpliesNotRelevant, 0);
        Assertions.notRelevantConstraints(notRelevantImpliesNotRelevant, 6);
        Assertions.constraintErrors(notRelevantImpliesNotRelevant, 0);
    }

    @Test
    public void testNegativeImplication() {
        // implication1 in {"A", "B"} => implication2 in {10, 20}
        // implicationSame1 in {"A"} => implicationSame2 == 10

        // true implies false
        BinaryConstraint trueImpliesFalse = new BinaryConstraint();
        trueImpliesFalse.implication1("A");
        trueImpliesFalse.implication2(30);
        trueImpliesFalse.implication("A", 20);

        Assertions.evaluatedConstraints(trueImpliesFalse, 6);
        Assertions.satisfiedConstraints(trueImpliesFalse, 0);
        Assertions.violatedConstraints(trueImpliesFalse, 2);
        Assertions.notRelevantConstraints(trueImpliesFalse, 4);
        Assertions.constraintErrors(trueImpliesFalse, 2);
    }

    @Test
    public void testPositiveOrConstraint() {
        // or1 in {"C", "D"} || or2 == 10
        // orSameStatement1 in {"C"} || orSameStatement2 in {10, 20}

        // true || true
        BinaryConstraint trueOrTrue = new BinaryConstraint();
        trueOrTrue.or1("D");
        trueOrTrue.or2(10);
        trueOrTrue.or("C", 10);

        Assertions.evaluatedConstraints(trueOrTrue, 6);
        Assertions.satisfiedConstraints(trueOrTrue, 2);
        Assertions.violatedConstraints(trueOrTrue, 0);
        Assertions.notRelevantConstraints(trueOrTrue, 4);
        Assertions.constraintErrors(trueOrTrue, 0);

        // true || false
        BinaryConstraint trueOrFalse = new BinaryConstraint();
        trueOrFalse.or1("D");
        trueOrFalse.or2(100);
        trueOrFalse.or("C", 100);

        Assertions.evaluatedConstraints(trueOrFalse, 6);
        Assertions.satisfiedConstraints(trueOrFalse, 2);
        Assertions.violatedConstraints(trueOrFalse, 0);
        Assertions.notRelevantConstraints(trueOrFalse, 4);
        Assertions.constraintErrors(trueOrFalse, 0);

        // false || true
        BinaryConstraint falseOrTrue = new BinaryConstraint();
        falseOrTrue.or1("Z");
        falseOrTrue.or2(10);
        falseOrTrue.or("Z", 10);

        Assertions.evaluatedConstraints(falseOrTrue, 6);
        Assertions.satisfiedConstraints(falseOrTrue, 2);
        Assertions.violatedConstraints(falseOrTrue, 0);
        Assertions.notRelevantConstraints(falseOrTrue, 4);
        Assertions.constraintErrors(falseOrTrue, 0);
    }

    @Test
    public void testNotRelevantOrConstraint() {
        // or1 in {"C", "D"} || or2 == 10
        // orSameStatement1 in {"C"} || orSameStatement2 in {10, 20}

        // true || not relevant
        BinaryConstraint trueOrNotRelevant = new BinaryConstraint();
        trueOrNotRelevant.or1("C");

        Assertions.evaluatedConstraints(trueOrNotRelevant, 6);
        Assertions.satisfiedConstraints(trueOrNotRelevant, 1);
        Assertions.violatedConstraints(trueOrNotRelevant, 0);
        Assertions.notRelevantConstraints(trueOrNotRelevant, 5);
        Assertions.constraintErrors(trueOrNotRelevant, 0);

        // false || not relevant
        BinaryConstraint falseOrNotRelevant = new BinaryConstraint();
        falseOrNotRelevant.or1("X");

        Assertions.evaluatedConstraints(falseOrNotRelevant, 6);
        Assertions.satisfiedConstraints(falseOrNotRelevant, 0);
        Assertions.violatedConstraints(falseOrNotRelevant, 1);
        Assertions.notRelevantConstraints(falseOrNotRelevant, 5);
        Assertions.constraintErrors(falseOrNotRelevant, 1);

        // not relevant || true
        BinaryConstraint notRelevantOrTrue = new BinaryConstraint();
        notRelevantOrTrue.or2(10);

        Assertions.evaluatedConstraints(notRelevantOrTrue, 6);
        Assertions.satisfiedConstraints(notRelevantOrTrue, 1);
        Assertions.violatedConstraints(notRelevantOrTrue, 0);
        Assertions.notRelevantConstraints(notRelevantOrTrue, 5);
        Assertions.constraintErrors(notRelevantOrTrue, 0);

        // not relevant || false
        BinaryConstraint notRelevantOrFalse = new BinaryConstraint();
        notRelevantOrFalse.or2(100);

        Assertions.evaluatedConstraints(notRelevantOrFalse, 6);
        Assertions.satisfiedConstraints(notRelevantOrFalse, 0);
        Assertions.violatedConstraints(notRelevantOrFalse, 1);
        Assertions.notRelevantConstraints(notRelevantOrFalse, 5);
        Assertions.constraintErrors(notRelevantOrFalse, 1);

        // not relevant || notRelevant
        BinaryConstraint notRelevantOrNotRelevant = new BinaryConstraint();

        Assertions.evaluatedConstraints(notRelevantOrNotRelevant, 6);
        Assertions.satisfiedConstraints(notRelevantOrNotRelevant, 0);
        Assertions.violatedConstraints(notRelevantOrNotRelevant, 0);
        Assertions.notRelevantConstraints(notRelevantOrNotRelevant, 6);
        Assertions.constraintErrors(notRelevantOrNotRelevant, 0);
    }

    @Test
    public void testNegativeOrConstraint() {
        // or1 in {"C", "D"} || or2 == 10
        // orSameStatement1 in {"C"} || orSameStatement2 in {10, 20}

        // false || false
        BinaryConstraint falseOrFalse = new BinaryConstraint();
        falseOrFalse.or1("X");
        falseOrFalse.or2(100);
        falseOrFalse.or("X", 100);

        Assertions.evaluatedConstraints(falseOrFalse, 6);
        Assertions.satisfiedConstraints(falseOrFalse, 0);
        Assertions.violatedConstraints(falseOrFalse, 2);
        Assertions.notRelevantConstraints(falseOrFalse, 4);
        Assertions.constraintErrors(falseOrFalse, 2);
    }

    @Test
    public void testPositiveAndConstraint() {
        // and1 in {"E", "F"} && and2 == 20
        // andSameStatement1 in {"E"} && andSameStatement2 != 20

        // true && true
        BinaryConstraint trueAndTrue = new BinaryConstraint();
        trueAndTrue.and1("E");
        trueAndTrue.and2(20);
        trueAndTrue.and("E", 10);

        Assertions.evaluatedConstraints(trueAndTrue, 6);
        Assertions.satisfiedConstraints(trueAndTrue, 2);
        Assertions.violatedConstraints(trueAndTrue, 0);
        Assertions.notRelevantConstraints(trueAndTrue, 4);
        Assertions.constraintErrors(trueAndTrue, 0);
    }

    @Test
    public void testNotRelevantAndConstraint() {
        // and1 in {"E", "F"} && and2 == 20
        // andSameStatement1 in {"E"} && andSameStatement2 != 20

        // true && not relevant
        BinaryConstraint trueAndNotRelevant = new BinaryConstraint();
        trueAndNotRelevant.and1("E");

        Assertions.evaluatedConstraints(trueAndNotRelevant, 6);
        Assertions.satisfiedConstraints(trueAndNotRelevant, 0);
        Assertions.violatedConstraints(trueAndNotRelevant, 0);
        Assertions.notRelevantConstraints(trueAndNotRelevant, 6);
        Assertions.constraintErrors(trueAndNotRelevant, 0);

        // false && not relevant
        BinaryConstraint falseAndNotRelevant = new BinaryConstraint();
        falseAndNotRelevant.and1("X");

        Assertions.evaluatedConstraints(falseAndNotRelevant, 6);
        Assertions.satisfiedConstraints(falseAndNotRelevant, 0);
        Assertions.violatedConstraints(falseAndNotRelevant, 0);
        Assertions.notRelevantConstraints(falseAndNotRelevant, 6);
        Assertions.constraintErrors(falseAndNotRelevant, 0);

        // not relevant && true
        BinaryConstraint notRelevantAndTrue = new BinaryConstraint();
        notRelevantAndTrue.and2(20);

        Assertions.evaluatedConstraints(notRelevantAndTrue, 6);
        Assertions.satisfiedConstraints(notRelevantAndTrue, 0);
        Assertions.violatedConstraints(notRelevantAndTrue, 0);
        Assertions.notRelevantConstraints(notRelevantAndTrue, 6);
        Assertions.constraintErrors(notRelevantAndTrue, 0);

        // not relevant && false
        BinaryConstraint notRelevantAndFalse = new BinaryConstraint();
        notRelevantAndFalse.and2(200);

        Assertions.evaluatedConstraints(notRelevantAndFalse, 6);
        Assertions.satisfiedConstraints(notRelevantAndFalse, 0);
        Assertions.violatedConstraints(notRelevantAndFalse, 0);
        Assertions.notRelevantConstraints(notRelevantAndFalse, 6);
        Assertions.constraintErrors(notRelevantAndFalse, 0);

        // not relevant && not relevant
        BinaryConstraint notRelevantAndNotRelevant = new BinaryConstraint();

        Assertions.evaluatedConstraints(notRelevantAndNotRelevant, 6);
        Assertions.satisfiedConstraints(notRelevantAndNotRelevant, 0);
        Assertions.violatedConstraints(notRelevantAndNotRelevant, 0);
        Assertions.notRelevantConstraints(notRelevantAndNotRelevant, 6);
        Assertions.constraintErrors(notRelevantAndNotRelevant, 0);
    }

    @Test
    public void testNegativeAndConstraint() {
        // and1 in {"E", "F"} && and2 == 20
        // andSameStatement1 in {"E"} && andSameStatement2 != 20

        // true && false
        BinaryConstraint trueAndFalse = new BinaryConstraint();
        trueAndFalse.and1("E");
        trueAndFalse.and2(200);
        trueAndFalse.and("E", 20);

        Assertions.evaluatedConstraints(trueAndFalse, 6);
        Assertions.satisfiedConstraints(trueAndFalse, 0);
        Assertions.violatedConstraints(trueAndFalse, 2);
        Assertions.notRelevantConstraints(trueAndFalse, 4);
        Assertions.constraintErrors(trueAndFalse, 2);

        // false && true
        BinaryConstraint falseAndTrue = new BinaryConstraint();
        falseAndTrue.and1("X");
        falseAndTrue.and2(20);
        falseAndTrue.and("X", 200);

        Assertions.evaluatedConstraints(falseAndTrue, 6);
        Assertions.satisfiedConstraints(falseAndTrue, 0);
        Assertions.violatedConstraints(falseAndTrue, 2);
        Assertions.notRelevantConstraints(falseAndTrue, 4);
        Assertions.constraintErrors(falseAndTrue, 2);

        // false && false
        BinaryConstraint falseAndFalse = new BinaryConstraint();
        falseAndFalse.and1("X");
        falseAndFalse.and2(200);
        falseAndFalse.and("X", 20);

        Assertions.evaluatedConstraints(falseAndFalse, 6);
        Assertions.satisfiedConstraints(falseAndFalse, 0);
        Assertions.violatedConstraints(falseAndFalse, 2);
        Assertions.notRelevantConstraints(falseAndFalse, 4);
        Assertions.constraintErrors(falseAndFalse, 2);
    }
}
