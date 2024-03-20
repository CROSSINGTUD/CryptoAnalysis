package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import typestate.finiteautomata.State;

import java.util.Collection;

public class ReportingErrorStateNode implements State {

	private Collection<Method> expectedCalls;

	public ReportingErrorStateNode(Collection<Method> expectedCalls) {
		this.expectedCalls = expectedCalls;
	}

	public Collection<Method> getExpectedCalls() {
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
		return obj instanceof ReportingErrorStateNode;
	}
}
