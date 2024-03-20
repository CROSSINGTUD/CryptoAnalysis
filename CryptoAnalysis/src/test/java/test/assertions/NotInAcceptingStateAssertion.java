package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import test.Assertion;
import typestate.finiteautomata.State;

public class NotInAcceptingStateAssertion implements Assertion {

	private Statement unit;
	private Val val;
	private boolean satisfied;

	public NotInAcceptingStateAssertion(Statement unit, Val accessGraph) {
		this.unit = unit;
		this.val = accessGraph;
	}

	public void computedResults(State s) {
		satisfied |= !s.isAccepting();
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
