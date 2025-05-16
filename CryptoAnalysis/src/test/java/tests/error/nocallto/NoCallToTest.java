/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.nocallto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDEFINED_PREDICATES)
public class NoCallToTest {

    @Test
    public void positivePredicateWithoutConditionTest() {
        NoCallTo noCallTo = new NoCallTo();

        // operation2 should not be called in any case
        noCallTo.operation2();

        Assertions.constraintErrors(noCallTo, 1);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        NoCallTo noCallTo = new NoCallTo();

        // operation2 is never called
        noCallTo.operation1();

        Assertions.constraintErrors(noCallTo, 0);
    }

    @Test
    public void positivePredicateWithConditionTest() {
        NoCallTo noCallTo = new NoCallTo(true);

        // Condition is satisfied => Call to operation3 not allowed
        noCallTo.operation3();

        Assertions.constraintErrors(noCallTo, 1);
    }

    @Test
    public void negativePredicateWithConditionTest() {
        NoCallTo noCallTo = new NoCallTo(false);

        // Condition is not satisfied => Call to operation3 is allowed
        noCallTo.operation3();

        Assertions.constraintErrors(noCallTo, 0);
    }
}
