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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

public class ForbiddenMethodError extends AbstractError {

    private final DeclaredMethod calledMethod;
    private final Collection<CrySLMethod> alternatives;

    public ForbiddenMethodError(
            IAnalysisSeed seed,
            Statement errorLocation,
            CrySLRule rule,
            DeclaredMethod calledMethod,
            Collection<CrySLMethod> alternatives) {
        super(seed, errorLocation, rule);

        this.calledMethod = calledMethod;
        this.alternatives = alternatives;
    }

    public Collection<CrySLMethod> getAlternatives() {
        return alternatives;
    }

    public DeclaredMethod getCalledMethod() {
        return calledMethod;
    }

    @Override
    public String toErrorMarkerString() {
        final StringBuilder msg = new StringBuilder();
        msg.append("Detected call to forbidden method ");
        msg.append(getCalledMethod().getSignature());
        msg.append(" of class ");
        msg.append(getCalledMethod().getDeclaringClass());

        if (!getAlternatives().isEmpty()) {
            msg.append(". Instead, call one of the methods ");
            String altMethods = CrySLUtils.formatMethodNames(alternatives);
            msg.append(altMethods);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), calledMethod, alternatives);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ForbiddenMethodError other
                && Objects.equals(calledMethod, other.getCalledMethod())
                && Objects.equals(alternatives, other.getAlternatives());
    }

    @Override
    public String toString() {
        return "ForbiddenMethodError: " + toErrorMarkerString();
    }
}
