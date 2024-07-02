package crypto.typestate;

import crypto.rules.CrySLMethod;
import typestate.finiteautomata.State;

import java.util.Set;

public class ReportingErrorStateNode implements State {

	private final Set<CrySLMethod> expectedCalls;

	public ReportingErrorStateNode(Set<CrySLMethod> expectedCalls) {
		this.expectedCalls = expectedCalls;
	}

	public Set<CrySLMethod> getExpectedCalls() {
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
