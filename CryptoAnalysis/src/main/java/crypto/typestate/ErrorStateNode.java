/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import typestate.finiteautomata.State;

/**
 * Final error state that cannot be left. All ingoing transitions should originate from a {@link
 * ReportingErrorStateNode}.
 */
public class ErrorStateNode implements State {

    public static final String LABEL = "ERR (final)";

    private static ErrorStateNode errorState;

    private ErrorStateNode() {}

    public static ErrorStateNode getInstance() {
        if (errorState == null) {
            errorState = new ErrorStateNode();
        }

        return errorState;
    }

    @Override
    public boolean isErrorState() {
        return true;
    }

    @Override
    public boolean isInitialState() {
        return false;
    }

    @Override
    public boolean isAccepting() {
        return false;
    }

    @Override
    public String toString() {
        return LABEL;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ErrorStateNode;
    }
}
