package test.assertions;

import test.Assertion;

public class PredicateContradiction implements Assertion{

	private boolean triggered;

	@Override
	public boolean isSatisfied() {
		return triggered;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	public void trigger(){
		triggered = true;
	}
}
