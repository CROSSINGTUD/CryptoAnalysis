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

import crysl.rule.CrySLMethod;
import java.util.Collection;
import typestate.finiteautomata.State;

/**
 * State that is directly reachable by a transitions from a non-error state. Whenever the analysis
 * is in this state, it reports a corresponding {@link crypto.analysis.errors.TypestateError}. All
 * further transitions should lead to the final {@link ErrorStateNode}.
 *
 * @param expectedCalls the methods expected to be called to not go into this error state
 */
public record ReportingErrorStateNode(Collection<CrySLMethod> expectedCalls) implements State {

    public static final String LABEL = "ERR";

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
}
