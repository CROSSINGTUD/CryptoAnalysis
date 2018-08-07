package crypto.typestate;

import java.util.Collection;

import soot.SootMethod;
import typestate.finiteautomata.State;

public class ErrorStateNode implements State {

	private Collection<SootMethod> expectedCalls;

	public ErrorStateNode(Collection<SootMethod> expectedCalls) {
		this.expectedCalls = expectedCalls;
	}

	public Collection<SootMethod> getExpectedCalls() {
		return expectedCalls;
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
		return "ERR";
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
