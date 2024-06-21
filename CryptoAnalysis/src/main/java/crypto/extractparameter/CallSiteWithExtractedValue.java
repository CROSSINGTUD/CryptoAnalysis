package crypto.extractparameter;

import boomerang.scene.Val;

import java.util.Arrays;

/**
 * Creates {@link CallSiteWithExtractedValue} a constructor with CallSiteWithParamIndex and ExtractedValue as parameter
 *	<p>
 *  CallSiteWithParamIndex gives position of the location index of the error<br>
 *  ExtractedValue gives the value of the call site
 *  </p>
 */

public class CallSiteWithExtractedValue {

	private final CallSiteWithParamIndex cs;
	private final ExtractedValue val;

	public CallSiteWithExtractedValue(CallSiteWithParamIndex cs, ExtractedValue val){
		this.cs = cs;
		this.val = val;
	}

	public CallSiteWithParamIndex getCallSite() {
		return cs;
	}

	public ExtractedValue getVal() {
		return val;
	}
	
	@Override
	public String toString() {
		String res;
		switch(cs.getIndex()) {
			case -1:
				return "Return value";
			case 0: 
				res = "First ";
				break;
			case 1: 
				res = "Second ";
				break;
			case 2: 
				res = "Third ";
				break;
			case 3: 
				res = "Fourth ";
				break;
			case 4: 
				res = "Fifth ";
				break;
			case 5: 
				res = "Sixth ";
				break;
			default:
				res = (cs.getIndex()+1) + "th ";
				break;
		}
		res += "parameter";
		if (val != null) {
			Val allocVal = val.getValue();
			if (allocVal.isConstant()) {
				res += " (with value " + allocVal.getVariableName() +")";
			}
		}
		return res;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{
				cs,
				val
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		CallSiteWithExtractedValue other = (CallSiteWithExtractedValue) obj;
		if (cs == null) {
			if (other.getCallSite() != null) return false;
		} else if (!cs.equals(other.getCallSite())) {
			return false;
		}

		if (val == null) {
			if (other.getVal() != null) return false;
		} else if (!val.equals(other.getVal())) {
			return false;
		}

		return true;
	}
}
