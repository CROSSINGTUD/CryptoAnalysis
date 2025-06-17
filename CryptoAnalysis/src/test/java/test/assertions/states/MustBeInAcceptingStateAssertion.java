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

public class MustBeInAcceptingStateAssertion extends StateResult {

    private boolean unsound;
    private boolean checked;

    public MustBeInAcceptingStateAssertion(Statement statement, Val seed) {
        super(statement, seed);

        this.unsound = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any state is not accepting
        for (State state : states) {
            unsound |= !state.isAccepting();
        }
        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || unsound;
    }

    @Override
    public String getErrorMessage() {
        if (checked) {
            return seed.getVariableName()
                    + " must be in an accepting state @ "
                    + statement
                    + " @ line "
                    + statement.getLineNumber();
        } else {
            return statement + " @ line " + statement.getLineNumber() + " has not been checked";
        }
    }
}
