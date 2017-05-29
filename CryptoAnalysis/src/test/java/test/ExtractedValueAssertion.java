package test;

import soot.Unit;

public class ExtractedValueAssertion implements Assertion {
	private Unit stmt;
	private int index;
	public ExtractedValueAssertion(Unit stmt, int index) {
		this.stmt = stmt;
		this.index = index;
	}
	@Override
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isImprecise() {
		return false;
	}

}
