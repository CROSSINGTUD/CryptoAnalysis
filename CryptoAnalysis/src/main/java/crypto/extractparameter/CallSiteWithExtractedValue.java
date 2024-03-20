package crypto.extractparameter;

import boomerang.scene.Val;

/**
 * Creates {@link CallSiteWithExtractedValue} a constructor with CallSiteWithParamIndex and ExtractedValue as parameter
 *
 *  CallSiteWithParamIndex gives position of the location index of the error
 *  ExtractedValue gives the value of the call site
 */

public class CallSiteWithExtractedValue {
	private CallSiteWithParamIndex cs;
	private ExtractedValue val;

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
		String res = "";
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
		if(val != null && val.getValue() != null){
			Val allocVal = val.getValue();
			if(allocVal.isConstant()){
				res += " (with value " + allocVal +")";
			}
		}
		return res;
	}
}
