package test.assertions;

import boomerang.jimple.Val;
import soot.Unit;
import test.Assertion;
import test.ComparableResult;
import typestate.finiteautomata.State;

public class InAcceptingStateAssertion implements Assertion, ComparableResult<State,Val> {

	private Unit unit;
	private Val val;
	private boolean satisfied;

	public InAcceptingStateAssertion(Unit unit, Val val) {
		this.unit = unit;
		this.val = val;
	}

	public void computedResults(State s) {
		satisfied |= s.isAccepting();
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

	@Override
	public String toString() {
		return "[" + getVal() + "@" + getStmt() + " must not be in error state]";
	}

	@Override
	public Val getVal() {
		return val;
	}
}
