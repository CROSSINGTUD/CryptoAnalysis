package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.typestate.ReportingErrorStateNode;
import test.Assertion;
import typestate.finiteautomata.State;

public class MustBeInState implements Assertion, StateResult {

	private final Statement unit;
	private final Val val;
	private final String state;
	private boolean satisfied;
	private int imprecise;

	public MustBeInState(Statement unit, Val val, String state) {
		this.unit = unit;
		this.val = val;
		this.state = state;
	}

	public void computedResults(State s) {
		if ((state.toString().equals("-1") && s instanceof ReportingErrorStateNode) || state.toString().equals(s.toString())) {
			satisfied = true;
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
		return val;
	}
	@Override
	public String toString() {
		return "["+getVal() + "@" + getStmt() + " must be in state "+ state+"]";
	}

}
