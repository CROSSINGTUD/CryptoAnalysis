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

import boomerang.scope.Statement;
import crypto.constraints.ComparisonConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import java.util.Collection;

/**
 * Represents a violated {@link ComparisonConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedComparisonConstraint(Statement statement, ComparisonConstraint constraint)
        implements ViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return getSimplifiedMessage(0);
    }

    @Override
    public String getSimplifiedMessage(int depth) {
        StringBuilder sb = new StringBuilder();

        Collection<ParameterWithExtractedValues> params =
                constraint.getExtractedValues().get(statement);
        for (ParameterWithExtractedValues param : params) {
            if (!constraint.getConstraint().getInvolvedVarNames().contains(param.varName())) {
                continue;
            }

            sb.append("\n")
                    .append("\t".repeat(depth))
                    .append("|- Extracted the following violating values for parameter ")
                    .append(" \"")
                    .append(param.param().getVariableName())
                    .append("\" (")
                    .append(param.varName())
                    .append(") @")
                    .append(param.statement())
                    .append(" @ line ")
                    .append(param.statement().getLineNumber())
                    .append(":");

            for (TransformedValue value : param.extractedValues()) {
                sb.append("\n")
                        .append("\t".repeat(depth + 1))
                        .append("|- Value ")
                        .append(value.getTransformedVal().getVariableName())
                        .append(" in class \"")
                        .append(value.getStatement().getMethod().getDeclaringClass())
                        .append("\" @")
                        .append(value.getStatement())
                        .append(" @ line ")
                        .append(value.getStatement().getLineNumber());
            }
        }

        return sb.toString();
    }
}
