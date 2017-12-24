package crypto.typestate;

import crypto.rules.StateNode;
import typestate.finiteautomata.State;

public class WrappedState implements State {
	private final StateNode delegate;
	private final boolean initialState;

	WrappedState(StateNode delegate, boolean initialState) {
		this.delegate = delegate;
		this.initialState = initialState;
	}

	public WrappedState(StateNode delegate) {
		this.delegate = delegate;
		this.initialState = false;
	}
	
	public StateNode delegate(){
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrappedState other = (WrappedState) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return delegate.getName().toString();
	}
}
