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

import crypto.constraints.BinaryConstraint;

/**
 * Represents the violation of a {@link BinaryConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedBinaryConstraint(BinaryConstraint constraint) implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        // TODO Create a detailed error message
        return "";
    }
}
