package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import test.Assertion;

import java.util.Map.Entry;

public class ExtractedValueAssertion implements Assertion {

	private final Statement stmt;
	private final int index;
	private boolean satisfied;

	public ExtractedValueAssertion(Statement stmt, int index) {
		this.stmt = stmt;
		this.index = index;
	}
	
	public void computedValues(Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues){
		for (Entry<CallSiteWithParamIndex, ExtractedValue> e: collectedValues.entries()) {
			Statement callSite = e.getKey().stmt();

			if (e.getValue().getValue().equals(Val.zero())) {
				continue;
			}

			if (callSite.equals(stmt) && e.getKey().getIndex() == index) {
				satisfied = true;
			}
		}
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
		return "Did not extract parameter with index: " + index + " @ " + stmt;
	}
}
