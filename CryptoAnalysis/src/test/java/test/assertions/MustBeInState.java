package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.typestate.ReportingErrorStateNode;
import test.Assertion;
import typestate.finiteautomata.State;

public class MustBeInState implements Assertion {

	private Statement unit;
	private Val accessGraph;
	private String state;
	private boolean satisfied;
	private int imprecise;

	public MustBeInState(Statement unit, Val accessGraph, String state) {
		this.unit = unit;
		this.accessGraph = accessGraph;
		this.state = state;
	}

	public void computedResults(State s) {
		if ((state.toString().equals("-1") && s instanceof ReportingErrorStateNode) || state.toString().equals(s.toString())) {
			satisfied |= true;
			imprecise++;
		} 
	}

	public Statement getStmt() {
		return unit;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return imprecise > 1;
	}

	public Val getVal() {
		return accessGraph;
	}
	@Override
	public String toString() {
		return "["+getVal() + "@" + getStmt() + " must be in state "+ state+"]";
	}

}
