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

public record SatisfiedValueConstraint(
        ValueConstraint constraint,
        ParameterWithExtractedValues parameter,
        Collection<TransformedValue> satisfiedValues)
        implements SatisfiedConstraint {

    @Override
    public String getSimplifiedMessage(int depth) {
        StringBuilder sb = new StringBuilder();

        for (TransformedValue value : satisfiedValues) {
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
                    .append(" satisfies the constraint @ ")
                    .append(parameter.statement())
                    .append(" @ line ")
                    .append(parameter.statement().getLineNumber());
        }

        return sb.toString();
    }
}
