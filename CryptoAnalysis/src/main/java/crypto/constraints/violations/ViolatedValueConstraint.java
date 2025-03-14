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
import crypto.extractparameter.ExtractedValue;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLValueConstraint;
import java.util.Collection;

/**
 * Represents the violation of a {@link ValueConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedValueConstraint(
        CrySLValueConstraint constraint, Collection<ExtractedValue> violatingValues, int index)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(CrySLUtils.getIndexAsString(index));

        if (violatingValues.size() > 1) {
            builder.append(" (with values ");

            String values =
                    String.join(
                            ", ",
                            violatingValues.stream()
                                    .map(value -> value.val().getVariableName())
                                    .toList());
            builder.append(values);
        } else {
            builder.append(" (with value ");

            String value = violatingValues.iterator().next().val().getVariableName();
            builder.append(value);
        }

        builder.append(")");
        builder.append(" should be any of ");
        builder.append("{");

        String expectedValues = getExpectedValuesAsString();
        builder.append(expectedValues);

        builder.append("}");

        return builder.toString();
    }

    private String getExpectedValuesAsString() {
        return String.join(", ", constraint.getValueRange());
    }
}
