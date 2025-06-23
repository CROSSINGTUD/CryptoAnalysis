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

import crypto.constraints.ValueConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import crypto.utils.CrySLUtils;
import java.util.Collection;

/**
 * Represents the violation of a {@link ValueConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedValueConstraint(
        ValueConstraint constraint,
        ParameterWithExtractedValues parameter,
        Collection<TransformedValue> violatingValues)
        implements ViolatedConstraint {

    @Override
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();

        for (TransformedValue value : violatingValues) {
            sb.append("\n|- ")
                    .append(CrySLUtils.getIndexAsString(parameter.index()))
                    .append(" \"")
                    .append(parameter.param().getVariableName())
                    .append("\" with value ")
                    .append(value.getTransformedVal().getVariableName())
                    .append(" should be any of {")
                    .append(getExpectedValuesAsString())
                    .append("} (extracted @ ")
                    .append(value.getStatement())
                    .append(" @ line ")
                    .append(value.getStatement().getLineNumber())
                    .append(")");
        }

        return sb.toString();
    }

    @Override
    public String getSimplifiedMessage(int depth) {
        StringBuilder sb = new StringBuilder();

        for (TransformedValue value : violatingValues) {
            sb.append("\n")
                    .append("\t".repeat(depth))
                    .append("|- ")
                    .append(CrySLUtils.getIndexAsString(parameter.index()))
                    .append(" \"")
                    .append(parameter.param().getVariableName())
                    .append("\" (")
                    .append(constraint.getConstraint().getVarName())
                    .append(") with value ")
                    .append(value.getTransformedVal().getVariableName())
                    .append(" violates the constraint @ ")
                    .append(parameter.statement())
                    .append(" @ line ")
                    .append(parameter.statement().getLineNumber());
        }

        return sb.toString();
    }

    private String getExpectedValuesAsString() {
        return String.join(", ", constraint.getConstraint().getValueRange());
    }
}
