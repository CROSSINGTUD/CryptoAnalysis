/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.lengthpred;

import java.util.UUID;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDEFINED_PREDICATES)
public class LengthTest {

    @Test
    public void positiveArrayLengthTest() {
        Length length = new Length();

        // length[arr] == 7
        byte[] arr = new byte[] {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
        length.lengthArray(arr);

        Assertions.constraintErrors(length, 0);
    }

    @Test
    public void negativeArrayLengthTest() {
        Length length = new Length();

        // length[arr] == 7
        byte[] arr = new byte[] {'a', 'b'};
        length.lengthArray(arr);

        Assertions.constraintErrors(length, 1);
    }

    @Test
    public void positiveStringLengthTest() {
        Length length = new Length();

        // length[s] == 5
        String s = "12345";
        length.lengthString(s);

        Assertions.constraintErrors(length, 0);
    }

    @Test
    public void negativeStringLengthTest() {
        Length length = new Length();

        // length[s] == 5
        String s = "1234567";
        length.lengthString(s);

        Assertions.constraintErrors(length, 1);
    }

    @Test
    public void positiveIntegerLengthTest() {
        Length length = new Length();

        // length[i] == 2
        int i = 12;
        length.lengthInteger(i);

        Assertions.constraintErrors(length, 0);
    }

    @Test
    public void negativeIntegerLengthTest() {
        Length length = new Length();

        // length[i] == 2
        int i = 123;
        length.lengthInteger(i);

        Assertions.constraintErrors(length, 1);
    }

    @Test
    public void unknownLengthTest() {
        byte[] arr = RandomUtils.secure().randomBytes(7);
        Length arrLength = new Length();
        arrLength.lengthArray(arr);

        String s = UUID.randomUUID().toString();
        Length stringLength = new Length();
        stringLength.lengthString(s);

        int i = (int) (Math.random() * 99) + 10;
        Length integerLength = new Length();
        integerLength.lengthInteger(i);

        Assertions.impreciseValueExtractionErrors(3);
    }
}
