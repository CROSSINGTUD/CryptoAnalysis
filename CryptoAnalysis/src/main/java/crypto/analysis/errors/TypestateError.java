/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis.errors;

import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

public class TypestateError extends AbstractOrderError {

    private final Collection<CrySLMethod> expectedMethodCalls;

    public TypestateError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<CrySLMethod> expectedMethodCalls) {
        super(seed, errorStmt, rule);

        this.expectedMethodCalls = expectedMethodCalls;
    }

    public Collection<CrySLMethod> getExpectedMethodCalls() {
        return expectedMethodCalls;
    }

    @Override
    public String toErrorMarkerString() {
        final StringBuilder msg = new StringBuilder();

        msg.append("Unexpected call to method \"");
        msg.append(getErrorStatement().getInvokeExpr().getDeclaredMethod().getName());
        msg.append("\"");
        msg.append(getObjectType());

        String altMethods = CrySLUtils.formatMethodNames(expectedMethodCalls);

        if (!altMethods.isEmpty()) {
            msg.append(". Expected a call to one of the following methods ");
            msg.append(altMethods);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expectedMethodCalls);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof TypestateError other
                && Objects.equals(expectedMethodCalls, other.getExpectedMethodCalls());
    }

    @Override
    public String toString() {
        return "TypestateError: " + toErrorMarkerString();
    }
}
