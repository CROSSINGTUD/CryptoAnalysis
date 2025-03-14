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

import crysl.rule.StateNode;
import java.util.Objects;
import typestate.finiteautomata.State;

/** State that wraps a {@link StateNode} from the state machine to make it consistent with IDEal. */
public class WrappedState implements State {

    private final StateNode delegate;
    private final boolean initialState;

    private WrappedState(StateNode delegate, boolean initialState) {
        this.delegate = delegate;
        this.initialState = initialState;
    }

    private WrappedState(StateNode delegate) {
        this.delegate = delegate;
        this.initialState = false;
    }

    public static WrappedState of(StateNode delegate, boolean initialState) {
        return new WrappedState(delegate, initialState);
    }

    public static WrappedState of(StateNode delegate) {
        return new WrappedState(delegate);
    }

    public StateNode delegate() {
        return delegate;
    }

    @Override
    public boolean isErrorState() {
        return delegate.isErrorState();
    }

    @Override
    public boolean isAccepting() {
        return delegate.getAccepting();
    }

    @Override
    public boolean isInitialState() {
        return initialState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedState other && Objects.equals(delegate, other.delegate());
    }

    @Override
    public String toString() {
        return delegate.getName();
    }
}
