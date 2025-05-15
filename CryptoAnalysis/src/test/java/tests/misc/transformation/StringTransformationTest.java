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

import java.util.UUID;
import org.junit.jupiter.api.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class StringTransformationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "transformation";
    }

    @Test
    public void positiveReplaceCharSequenceTest() {
        // Test replace(CharSequence, CharSequence) with replacing incorrect to correct String
        String string = "DES".replace("D", "A");

        StringConstraint constraint = new StringConstraint();
        constraint.replaceConstraint(string);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeReplaceCharSequenceTest() {
        // Test replace(CharSequence, CharSequence) with replacing correct with incorrect String
        String string = "AES".replace("A", "D");

        StringConstraint constraint = new StringConstraint();
        constraint.replaceConstraint(string);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(constraint, 1);
    }

    @Test
    public void cascadingReplaceCharSequenceTest() {
        // Correct -> incorrect -> correct (AES -> DES -> AES)
        String incorrect1 = "AES".replace("A", "D");
        String correct1 = incorrect1.replace("D", "A");

        StringConstraint constraint1 = new StringConstraint();
        constraint1.replaceConstraint(correct1);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint1, 0);

        // Incorrect -> correct -> incorrect (DES -> AES -> DES)
        String correct2 = "DES".replace("D", "A");
        String incorrect2 = correct2.replace("A", "D");

        StringConstraint constraint2 = new StringConstraint();
        constraint2.replaceConstraint(incorrect2);
        Assertions.extValue(0);
        Assertions.constraintErrors(constraint2, 1);
    }

    @Test
    public void positiveToCharArrayTest() {
        // Test toCharArray() with a random String -> not hard coded
        char[] password = UUID.randomUUID().toString().toCharArray();

        StringConstraint constraint = new StringConstraint();
        constraint.toCharArrayConstraint(password);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeToCharArrayTest() {
        // Test toCharArray() with a fixed String -> hard coded
        char[] password = "password".toCharArray();

        StringConstraint constraint = new StringConstraint();
        constraint.toCharArrayConstraint(password);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 1);
    }

    @Test
    public void positiveGetBytesTest() {
        // Test getBytes() with a random String -> not hard coded
        byte[] password = UUID.randomUUID().toString().getBytes();

        StringConstraint constraint = new StringConstraint();
        constraint.getBytesConstraint(password);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeGetBytesTest() {
        // Test getBytes() with a fixed String -> hard coded
        byte[] password = "password".getBytes();

        StringConstraint constraint = new StringConstraint();
        constraint.getBytesConstraint(password);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 1);
    }
}
