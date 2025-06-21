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
import crypto.constraints.violations.ImpreciseValueConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class ImpreciseValueExtractionError extends AbstractConstraintsError {

    private final ImpreciseValueConstraint violatedConstraint;
    private final ParameterWithExtractedValues parameter;

    public ImpreciseValueExtractionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            ImpreciseValueConstraint violatedConstraint,
            ParameterWithExtractedValues parameter) {
        super(seed, errorStmt, rule);

        this.violatedConstraint = violatedConstraint;
        this.parameter = parameter;
    }

    public ImpreciseValueConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    public ParameterWithExtractedValues getParameter() {
        return parameter;
    }

    @Override
    public String toErrorMarkerString() {
        return "Could not extract a value for parameter \""
                + parameter.param()
                + "\" ("
                + parameter.varName()
                + ") to evaluate the constraint \""
                + violatedConstraint
                + "\":"
                + violatedConstraint.getErrorMessage();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), violatedConstraint, parameter);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ImpreciseValueExtractionError other
                && Objects.equals(violatedConstraint, other.getViolatedConstraint())
                && Objects.equals(parameter, other.getParameter());
    }

    @Override
    public String toString() {
        return "ImpreciseValueExtractionError: " + toErrorMarkerString();
    }
}
