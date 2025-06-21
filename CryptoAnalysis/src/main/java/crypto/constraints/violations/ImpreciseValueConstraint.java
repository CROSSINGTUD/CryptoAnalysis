/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.constraints.violations;

import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import java.util.Collection;

public record ImpreciseValueConstraint(
        EvaluableConstraint constraint,
        ParameterWithExtractedValues parameter,
        Collection<TransformedValue> impreciseValues) {

    public String getErrorMessage() {
        return getSimplifiedMessage(0);
    }

    public String getSimplifiedMessage(int depth) {
        StringBuilder sb = new StringBuilder();
        for (TransformedValue value : impreciseValues) {
            preOrderTransformedValues(sb, value, depth);
        }

        return sb.toString();
    }

    private void preOrderTransformedValues(StringBuilder sb, TransformedValue value, int depth) {
        sb.append("\n");
        sb.append("\t".repeat(depth));
        sb.append("|- Could not evaluate expression \"")
                .append(value.getTransformedVal())
                .append("\" in class \"")
                .append(
                        value.getStatement()
                                .getMethod()
                                .getDeclaringClass()
                                .getFullyQualifiedName())
                .append("\" @ ")
                .append(value.getStatement())
                .append(" @ line ")
                .append(value.getStatement().getLineNumber());

        for (TransformedValue unknownValue : value.getUnknownValues()) {
            preOrderTransformedValues(sb, unknownValue, depth + 1);
        }
    }

    @Override
    public String toString() {
        return constraint.toString();
    }
}
