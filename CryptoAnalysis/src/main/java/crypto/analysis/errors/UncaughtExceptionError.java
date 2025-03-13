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
import boomerang.scope.WrappedClass;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class UncaughtExceptionError extends AbstractError {

    private final WrappedClass exception;

    public UncaughtExceptionError(
            IAnalysisSeed seed, Statement errorStmt, CrySLRule rule, WrappedClass exception) {
        super(seed, errorStmt, rule);
        this.exception = exception;
    }

    public WrappedClass getException() {
        return exception;
    }

    @Override
    public String toErrorMarkerString() {
        return String.format("Uncaught exception `%s`", exception.getFullyQualifiedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exception);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof UncaughtExceptionError other
                && Objects.equals(exception, other.getException());
    }
}
