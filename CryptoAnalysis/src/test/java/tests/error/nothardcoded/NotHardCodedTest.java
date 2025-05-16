/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.nothardcoded;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDEFINED_PREDICATES)
public class NotHardCodedTest {

    @Test
    public void positivePredicateWithIntValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // Parameter is random
        int value = (int) (Math.random() * 10);
        notHardCoded.operation(value);

        Assertions.constraintErrors(notHardCoded, 0);
    }

    @Test
    public void negativePredicateWithIntValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // Parameter is hard coded
        notHardCoded.operation(12345);

        Assertions.constraintErrors(notHardCoded, 1);
    }

    @Test
    public void positivePredicateWithStringValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        String value = UUID.randomUUID().toString();
        notHardCoded.operation(value);

        Assertions.constraintErrors(notHardCoded, 0);
    }

    @Test
    public void negativePredicateWithStringValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        String value = "This is hard coded";
        notHardCoded.operation(value);

        Assertions.constraintErrors(notHardCoded, 1);
    }

    @Test
    public void predicateWithInstanceTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // BigInteger's value is random, the instance is hard coded
        BigInteger bigInteger = new BigInteger(8, new Random());
        notHardCoded.operation(bigInteger);

        Assertions.constraintErrors(notHardCoded, 1);
    }

    @Test
    public void positivePredicateWithArrayTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        Random random = new Random();
        int[] array = IntStream.generate(random::nextInt).limit(10).toArray();

        notHardCoded.operation(array);

        Assertions.constraintErrors(notHardCoded, 0);
    }

    @Test
    public void negativePredicateWithArrayTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        char[] array = new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        notHardCoded.operation(array);

        Assertions.constraintErrors(notHardCoded, 1);
    }
}
