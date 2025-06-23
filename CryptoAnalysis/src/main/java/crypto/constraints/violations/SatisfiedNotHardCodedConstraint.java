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

import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import crypto.utils.CrySLUtils;

public record SatisfiedNotHardCodedConstraint(
        ParameterWithExtractedValues parameter, TransformedValue value)
        implements SatisfiedConstraint {

    @Override
    public String getSimplifiedMessage(int depth) {
        return "\n"
                + "\t".repeat(depth)
                + "|- "
                + CrySLUtils.getIndexAsString(parameter.index())
                + " @ "
                + parameter.statement()
                + " is hard coded";
    }
}
