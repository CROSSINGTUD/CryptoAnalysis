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
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class ImpreciseValueExtractionError extends AbstractConstraintsError {

    private final EvaluableConstraint violatedConstraint;

    public ImpreciseValueExtractionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            EvaluableConstraint constraint) {
        super(seed, errorStmt, rule);
        this.violatedConstraint = constraint;
    }

    public ImpreciseValueExtractionError(
            AnalysisSeedWithSpecification seed,
            Statement statement,
            CrySLRule rule,
            ParameterWithExtractedValues parameter,
            TransformedValue value,
            EvaluableConstraint constraint) {
        super(seed, statement, rule);

        violatedConstraint = constraint;
    }

    public EvaluableConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        return "Constraint \""
                + violatedConstraint
                + "\" could not be evaluated due to insufficient information.";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ImpreciseValueExtractionError other
                && Objects.equals(violatedConstraint, other.getViolatedConstraint());
    }

    @Override
    public String toString() {
        return "ImpreciseValueExtractionError: " + toErrorMarkerString();
    }
}
