/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.misc.transformation;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class WrapperTransformationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "transformation";
    }

    @Test
    public void positiveIntegerParseIntTest() {
        int correctValue = Integer.parseInt("10");

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(correctValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeIntegerParseIntTest() {
        int incorrectValue = Integer.parseInt("9999");

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(incorrectValue);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(constraint, 1);
    }

    @Test
    public void branchingIntegerParseIntTest() {
        String value = "10";
        if (Math.random() > 0.5) {
            value = "9999";
        }

        int intValue = Integer.parseInt(value);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(intValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 1);
    }

    @Test
    public void positiveBigIntegerConstructorTest() {
        BigInteger correctValue1 = new BigInteger("999999");

        WrapperConstraint constraint1 = new WrapperConstraint();
        constraint1.bigIntegerConstructor(correctValue1);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint1, 0);

        BigInteger correctValue2 = new BigInteger("F423F", 16); // 999.999

        WrapperConstraint constraint2 = new WrapperConstraint();
        constraint2.bigIntegerConstructor(correctValue2);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint2, 0);
    }

    @Test
    public void negativeBigIntegerConstructorTest() {
        BigInteger incorrectValue1 = new BigInteger("111111");

        WrapperConstraint constraint1 = new WrapperConstraint();
        constraint1.bigIntegerConstructor(incorrectValue1);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint1, 1);

        BigInteger correctValue2 = new BigInteger("3640E", 16); // 222.222

        WrapperConstraint constraint2 = new WrapperConstraint();
        constraint2.bigIntegerConstructor(correctValue2);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint2, 1);
    }

    @Test
    public void positiveBigIntegerValueOfTest() {
        BigInteger correctValue = BigInteger.valueOf(100000);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.bigIntegerValueOfConstraint(correctValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeBigIntegerValueOfTest() {
        BigInteger incorrectValue = BigInteger.valueOf(10);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.bigIntegerValueOfConstraint(incorrectValue);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(constraint, 1);
    }
}
