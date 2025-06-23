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

import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;

/**
 * Represents a violation of the predefined predicate 'noCallTo[$methods]'
 *
 * @param method the methods that must not be called
 */
public record ViolatedNoCallToConstraint(CrySLMethod method) implements ViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return "Call to " + CrySLUtils.formatMethodName(method) + " not allowed";
    }

    @Override
    public String getSimplifiedMessage(int depth) {
        return "\n"
                + "\t".repeat(depth)
                + "Call to "
                + CrySLUtils.formatMethodName(method)
                + " not allowed";
    }
}
