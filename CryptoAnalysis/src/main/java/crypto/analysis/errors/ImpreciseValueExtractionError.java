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
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.TransformedValue;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class ImpreciseValueExtractionError extends AbstractConstraintsError {

    private final EvaluableConstraint violatedConstraint;
    private final TransformedValue value;

    public ImpreciseValueExtractionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            EvaluableConstraint constraint,
            TransformedValue value) {
        super(seed, errorStmt, rule);
        this.violatedConstraint = constraint;
        this.value = value;
    }

    public EvaluableConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Constraint \"")
                .append(violatedConstraint)
                .append("\" could not be evaluated due to the following reasons:");
        preOrderTransformedValues(sb, value, 0);

        return sb.toString();
    }

    private void preOrderTransformedValues(StringBuilder sb, TransformedValue value, int depth) {
        sb.append("\n");
        sb.append("\t".repeat(depth));
        sb.append("|- Could not evaluate expression \"")
                .append(value.getTransformedVal())
                .append("\" in class \"")
                .append(value.getTransformedVal().m().getDeclaringClass())
                .append("\" @ ")
                .append(value.getStatement())
                .append(" @ line ")
                .append(value.getStatement().getLineNumber());

        for (TransformedValue unknownValue : value.getUnknownValues()) {
            preOrderTransformedValues(sb, unknownValue, depth + 1);
        }
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
