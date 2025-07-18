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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.TRANSFORMATION)
public class OperatorTransformationTest {

    @Test
    public void positiveLengthExprTest() {
        byte[] correctBytes = new byte[] {'t', 'e', 's', 't'};

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeLengthExprTest() {
        byte[] incorrectBytes = new byte[] {'t', 'e', 's', 't', 'i', 'n', 'g'};

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(incorrectBytes.length);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(constraint, 1);
    }

    @Test
    public void positiveLengthFromStringTest() {
        byte[] correctBytes = "four".getBytes();

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);

        Assertions.constraintErrors(constraint, 0);
    }

    @Test
    public void negativeLengthFromStringTest() {
        byte[] correctBytes = "notFour".getBytes();

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(constraint, 1);
    }
}
