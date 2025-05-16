/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.nevertypeof;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDEFINED_PREDICATES)
public class NeverTypeOfTest {

    @Test
    public void positivePredicateWithoutConditionTest() {
        NeverTypeOf neverTypeOf = new NeverTypeOf();

        char[] value = new char[] {'a', 'l', 'l', 'o', 'w', 'e', 'd'};
        neverTypeOf.operation(value);

        Assertions.constraintErrors(neverTypeOf, 0);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        NeverTypeOf neverTypeOf = new NeverTypeOf();
        neverTypeOf.operation("notAllowed");

        Assertions.constraintErrors(neverTypeOf, 1);
    }

    @Test
    public void toCharArrayTest() {
        // Special case: toCharArray() is transformed from a string
        String string = "password";
        char[] transformedString = string.toCharArray();

        NeverTypeOf neverTypeOf = new NeverTypeOf();
        neverTypeOf.operation(transformedString);
        Assertions.extValue(0);

        Assertions.constraintErrors(neverTypeOf, 1);
    }
}
