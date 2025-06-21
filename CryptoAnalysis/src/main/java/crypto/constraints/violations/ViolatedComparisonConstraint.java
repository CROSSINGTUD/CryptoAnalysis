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
import crypto.utils.CrySLUtils;
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
            sb.append("\n")
                    .append("\t".repeat(depth))
                    .append("|- ")
                    .append(CrySLUtils.getIndexAsString(param.index()))
                    .append(" \"")
                    .append(param.param().getVariableName())
                    .append("\" (")
                    .append(param.varName())
                    .append(") evaluates to:");

            for (TransformedValue value : param.extractedValues()) {
                sb.append("\n")
                        .append("\t".repeat(depth + 1))
                        .append("|- ")
                        .append(value.getTransformedVal().getVariableName())
                        .append(" @ ")
                        .append(value.getStatement())
                        .append(" @ line ")
                        .append(value.getStatement().getLineNumber())
                        .append(" in class \"")
                        .append(value.getStatement().getMethod().getDeclaringClass())
                        .append("\"");
            }
        }

        return sb.toString();
    }
}
