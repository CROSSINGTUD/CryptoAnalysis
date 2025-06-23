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

public class DiscoveredRuleSeedAssertion extends AbstractDiscoveredSeedAssertion {

    public DiscoveredRuleSeedAssertion(int expectedCount) {
        super(expectedCount);
    }

    @Override
    public String getErrorMessage() {
        return "Expected to discover "
                + expectedCount
                + " seeds from rules, but got "
                + actualCount;
    }
}
