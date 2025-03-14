/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.assertions;

import boomerang.scope.Val;
import java.util.Collection;

public class ConstraintsEvaluatedAssertion implements Assertion {

    private final Val val;
    private final int expectedConstraints;
    private int actualConstraints;

    public ConstraintsEvaluatedAssertion(Val val, int expectedConstraints) {
        this.val = val;
        this.expectedConstraints = expectedConstraints;

        actualConstraints = 0;
    }

    public void reported(Collection<Val> seed) {
        if (seed.contains(val)) {
            actualConstraints++;
        }
    }

    @Override
    public boolean isUnsound() {
        return expectedConstraints > actualConstraints;
    }

    @Override
    public boolean isImprecise() {
        return expectedConstraints < actualConstraints;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedConstraints
                + " evaluated constraints on object "
                + val.getVariableName()
                + ", but got "
                + actualConstraints;
    }
}
