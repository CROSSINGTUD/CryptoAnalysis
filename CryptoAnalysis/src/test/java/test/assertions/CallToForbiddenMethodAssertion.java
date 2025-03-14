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

import boomerang.scope.Statement;

public class CallToForbiddenMethodAssertion implements Assertion {

    private final Statement stmt;
    private boolean satisfied;

    public CallToForbiddenMethodAssertion(Statement stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isUnsound() {
        return !satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "Expected to report a call to a forbidden method at this statement: " + stmt;
    }

    public void reported(Statement callSite) {
        satisfied |= callSite.equals(stmt);
    }
}
