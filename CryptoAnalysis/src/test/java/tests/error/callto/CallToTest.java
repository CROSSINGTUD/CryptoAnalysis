/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.callto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDEFINED_PREDICATES)
public class CallToTest {

    @Test
    public void positivePredicateWithoutCondition() {
        // Required call to operation2
        CallTo callTo = new CallTo();
        callTo.operation2();

        Assertions.constraintErrors(callTo, 0);
    }

    @Test
    public void negativePredicateWithoutCondition() {
        // Missing required call to operation2
        CallTo callTo = new CallTo();
        callTo.operation1();

        Assertions.constraintErrors(callTo, 1);
    }

    @Test
    public void positivePredicateWithCondition() {
        // Condition is satisfied => Required call to operation3
        CallTo callTo1 = new CallTo(true);
        callTo1.operation2();
        callTo1.operation3();

        // Condition is not satisfied => Call to operation3 not required
        CallTo callTo2 = new CallTo(false);
        callTo2.operation2();

        Assertions.constraintErrors(callTo1, 0);
        Assertions.constraintErrors(callTo2, 0);
    }

    @Test
    public void negativePredicateWithCondition() {
        // Condition is satisfied, but no call to operation3
        CallTo callTo = new CallTo(true);
        callTo.operation2();

        Assertions.constraintErrors(callTo, 1);
    }
}
