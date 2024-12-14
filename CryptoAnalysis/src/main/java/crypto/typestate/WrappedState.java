package crypto.typestate;

import crysl.rule.StateNode;
import java.util.Objects;
import typestate.finiteautomata.State;

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
