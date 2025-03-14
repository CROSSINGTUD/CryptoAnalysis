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

public class ConstraintErrorCountAssertion implements Assertion {

    private final Val seed;
    private final int expectedErrorCounts;
    private int actualErrorCounts;

    public ConstraintErrorCountAssertion(Val seed, int numberOfCounts) {
        this.seed = seed;
        this.expectedErrorCounts = numberOfCounts;
    }

    public void increaseCount(Collection<Val> vals) {
        if (vals.contains(seed)) {
            actualErrorCounts++;
        }
    }

    @Override
    public boolean isUnsound() {
        return expectedErrorCounts > actualErrorCounts;
    }

    @Override
    public boolean isImprecise() {
        return expectedErrorCounts < actualErrorCounts;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedErrorCounts
                + " constraint errors on object "
                + seed.getVariableName()
                + ", but got "
                + actualErrorCounts;
    }
}
