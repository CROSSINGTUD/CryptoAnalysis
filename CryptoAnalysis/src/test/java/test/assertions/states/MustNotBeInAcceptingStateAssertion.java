/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public class MustNotBeInAcceptingStateAssertion extends StateResult {

    private boolean unsound;
    private boolean checked;

    public MustNotBeInAcceptingStateAssertion(Statement statement, Val val) {
        super(statement, val);

        this.unsound = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any state is accepting
        for (State state : states) {
            unsound |= state.isAccepting();
        }
        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || unsound;
    }

    @Override
    public String getErrorMessage() {
        return seed.getVariableName()
                + " @ "
                + statement
                + " @ line "
                + statement.getLineNumber()
                + " must not be in accepting state";
    }
}
