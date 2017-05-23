package test;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import soot.Unit;
import typestate.TypestateDomainValue;

public class MustBeInState implements IExpectedResults<StateNode> {

	private Unit unit;
	private AccessGraph accessGraph;
	private String state;
	private boolean satisfied;
	private boolean imprecise;

	MustBeInState(Unit unit, AccessGraph accessGraph, String state) {
		this.unit = unit;
		this.accessGraph = accessGraph;
		this.state = state;
	}

	@Override
	public void computedResults(TypestateDomainValue<StateNode> results) {
		if(state.toString().equals("-1")){
			satisfied = true;
			imprecise = results.getStates().size() > 0;
			return;
		}
		for (StateNode s : results.getStates()) {
			if (state.toString().equals(s.getName().toString())) {
				satisfied |= true;
				imprecise = results.getStates().size() > 1;
			} 
		}
	}

	@Override
	public Unit getStmt() {
		return unit;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return imprecise;
	}

	@Override
	public AccessGraph getAccessGraph() {
		return accessGraph;
	}
	@Override
	public String toString() {
		return "["+getAccessGraph() + "@" + getStmt() + " must be in state "+ state+"]";
	}
}
