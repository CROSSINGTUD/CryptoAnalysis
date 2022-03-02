package test.assertions;

import test.Assertion;

public class PredicateContradiction implements Assertion{

	private boolean triggered;
	boolean shouldBeContradiced = true;
	
	public PredicateContradiction() {
		super();
	}
	
	public PredicateContradiction(boolean shouldBeContradiced) {
		super();
		this.shouldBeContradiced = shouldBeContradiced;
	}

	@Override
	public boolean isSatisfied() {
		return shouldBeContradiced == triggered;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	public void trigger(){
		triggered = true;
	}
}
