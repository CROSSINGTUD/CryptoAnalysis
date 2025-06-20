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
import crypto.extractparameter.TransformedValue;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLValueConstraint;
import java.util.Collection;

/**
 * Represents the violation of a {@link ValueConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedValueConstraint(
        CrySLValueConstraint constraint, Collection<TransformedValue> violatingValues, int index)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();

        for (TransformedValue value : violatingValues) {
            sb.append("\n|- ")
                    .append(CrySLUtils.getIndexAsString(index))
                    .append(" with value ")
                    .append(value.getTransformedVal().getVariableName())
                    .append(" should be any of {")
                    .append(getExpectedValuesAsString())
                    .append("} (")
                    .append(value.getStatement())
                    .append(" @ line ")
                    .append(value.getStatement().getLineNumber())
                    .append(")");
        }

        return sb.toString();
    }

    private String getExpectedValuesAsString() {
        return String.join(", ", constraint.getValueRange());
    }
}
