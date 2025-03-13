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

import boomerang.scope.Method;
import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public abstract class AbstractError {

    private final IAnalysisSeed seed;
    private final Statement errorStmt;
    private final CrySLRule rule;

    private final Collection<AbstractError> precedingErrors;
    private final Collection<AbstractError> subsequentErrors;

    public AbstractError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule) {
        this.seed = seed;
        this.errorStmt = errorStmt;
        this.rule = rule;

        this.precedingErrors = new HashSet<>();
        this.subsequentErrors = new HashSet<>();
    }

    public abstract String toErrorMarkerString();

    public IAnalysisSeed getSeed() {
        return seed;
    }

    public Statement getErrorStatement() {
        return errorStmt;
    }

    public CrySLRule getRule() {
        return rule;
    }

    public Method getMethod() {
        return errorStmt.getMethod();
    }

    public int getLineNumber() {
        return errorStmt.getStartLineNumber();
    }

    public void addPrecedingError(AbstractError error) {
        precedingErrors.add(error);
    }

    public void addCausingError(Collection<AbstractError> parents) {
        precedingErrors.addAll(parents);
    }

    public void addSubsequentError(AbstractError subsequentError) {
        subsequentErrors.add(subsequentError);
    }

    public Collection<AbstractError> getPrecedingErrors() {
        return precedingErrors;
    }

    public Collection<AbstractError> getSubsequentErrors() {
        return subsequentErrors;
    }

    public Collection<AbstractError> getRootErrors() {
        return this.precedingErrors;
    }

    public String toString() {
        return toErrorMarkerString();
    }

    protected String getObjectType() {
        return " on object of type " + seed.getFact().getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed, errorStmt, rule);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractError other
                && Objects.equals(seed, other.seed)
                && Objects.equals(errorStmt, other.errorStmt)
                && Objects.equals(rule, other.rule);
    }
}
