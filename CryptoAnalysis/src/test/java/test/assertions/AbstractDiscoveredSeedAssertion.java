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

public abstract class AbstractDiscoveredSeedAssertion implements Assertion {

    protected final int expectedCount;
    protected int actualCount;

    protected AbstractDiscoveredSeedAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
        this.actualCount = 0;
    }

    public void increaseCount() {
        actualCount++;
    }

    @Override
    public boolean isUnsound() {
        return actualCount < expectedCount;
    }

    @Override
    public boolean isImprecise() {
        return actualCount > expectedCount;
    }
}
