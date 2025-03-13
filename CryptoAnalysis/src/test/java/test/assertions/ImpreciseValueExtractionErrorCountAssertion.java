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

public class ImpreciseValueExtractionErrorCountAssertion implements Assertion {

    private final int expectedErrorCount;
    private int actualErrorCount;

    public ImpreciseValueExtractionErrorCountAssertion(int expectedErrorCount) {
        this.expectedErrorCount = expectedErrorCount;
    }

    public void increaseCount() {
        actualErrorCount++;
    }

    @Override
    public boolean isUnsound() {
        return expectedErrorCount > actualErrorCount;
    }

    @Override
    public boolean isImprecise() {
        return expectedErrorCount < actualErrorCount;
    }

    @Override
    public String getErrorMessage() {
        return "Expected "
                + expectedErrorCount
                + " imprecise value extraction errors, but got "
                + actualErrorCount;
    }
}
