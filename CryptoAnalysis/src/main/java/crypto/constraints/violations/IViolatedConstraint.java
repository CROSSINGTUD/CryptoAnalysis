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

/**
 * Super class for all violated constraints. A subclass has to provide a description of the
 * violation that is included in the reports.
 */
public interface IViolatedConstraint {

    /**
     * Error message for the violated constraint that is included in the reports
     *
     * @return the error message
     */
    String getErrorMessage();
}
