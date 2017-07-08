package test.assertions;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import soot.Unit;
import test.Assertion;
import test.ComparableResult;
import typestate.TypestateDomainValue;

public class NotInAcceptingStateAssertion implements Assertion, ComparableResult<StateNode> {

	private Unit unit;
	private AccessGraph accessGraph;
	private boolean satisfied;

	public NotInAcceptingStateAssertion(Unit unit, AccessGraph accessGraph) {
		this.unit = unit;
		this.accessGraph = accessGraph;
	}

	public void computedResults(TypestateDomainValue<StateNode> results) {
		for(StateNode s : results.getStates())
			satisfied |= !s.getAccepting();
	}

	public Unit getStmt() {
		return unit;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	public AccessGraph getAccessGraph() {
		return accessGraph;
	}

	@Override
	public String toString() {
		return "[" + getAccessGraph() + "@" + getStmt() + " must not be in error state]";
	}
}
