package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import test.Assertion;
import typestate.finiteautomata.State;

public class InAcceptingStateAssertion implements Assertion, StateResult {

	private final Statement unit;
	private final Val val;
	private boolean satisfied;

	public InAcceptingStateAssertion(Statement unit, Val val) {
		this.unit = unit;
		this.val = val;
	}

	public Val getVal() {
		return val;
	}

	public Statement getStmt() {
		return unit;
	}

	public void computedResults(State s) {
		satisfied |= s.isAccepting();
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	@Override
	public String toString() {
		return "[" + val + "@" + unit + " must not be in error state]";
	}

}
