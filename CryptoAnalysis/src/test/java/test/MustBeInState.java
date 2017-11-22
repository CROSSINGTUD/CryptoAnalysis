package test;

import boomerang.jimple.Val;
import crypto.typestate.ErrorStateNode;
import soot.Unit;
import typestate.TransitionFunction;
import typestate.finiteautomata.State;

public class MustBeInState implements Assertion, ComparableResult<TransitionFunction,Val> {

	private Unit unit;
	private Val accessGraph;
	private String state;
	private boolean satisfied;
	private boolean imprecise;

	MustBeInState(Unit unit, Val accessGraph, String state) {
		this.unit = unit;
		this.accessGraph = accessGraph;
		this.state = state;
	}

	public void computedResults(TransitionFunction results) {
		for (State s : results.getStates()) {
			if ((state.toString().equals("-1") && s.equals(ErrorStateNode.v())) || state.toString().equals(s.toString())) {
				satisfied |= true;
				imprecise = results.getStates().size() > 1;
			} 
		}
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
		return imprecise;
	}

	public Val getVal() {
		return accessGraph;
	}
	@Override
	public String toString() {
		return "["+getVal() + "@" + getStmt() + " must be in state "+ state+"]";
	}

}
