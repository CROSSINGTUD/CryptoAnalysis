package crypto.extractparameter;

import boomerang.scene.Val;

import java.util.Arrays;

public class CallSiteWithExtractedValue {

    private final CallSiteWithParamIndex callSiteWithParam;
    private final ExtractedValue extractedValue;

    public CallSiteWithExtractedValue(CallSiteWithParamIndex callSiteWithParam, ExtractedValue extractedValue) {
        this.callSiteWithParam = callSiteWithParam;
        this.extractedValue = extractedValue;
    }

    public CallSiteWithParamIndex getCallSiteWithParam() {
        return callSiteWithParam;
    }

    public ExtractedValue getExtractedValue() {
        return extractedValue;
    }

    @Override
    public String toString() {
        String res;
        switch(callSiteWithParam.getIndex()) {
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
                res = (callSiteWithParam.getIndex() + 1) + "th ";
                break;
        }
        res += "parameter";
        if (extractedValue != null) {
            Val allocVal = extractedValue.getVal();

            if (allocVal.isConstant()) {
                res += " (with value " + allocVal.getVariableName() +")";
            }
        }
        return res;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{
                callSiteWithParam,
                extractedValue
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        CallSiteWithExtractedValue other = (CallSiteWithExtractedValue) obj;
        if (callSiteWithParam == null) {
            if (other.getCallSiteWithParam() != null) return false;
        } else if (!callSiteWithParam.equals(other.getCallSiteWithParam())) {
            return false;
        }

        if (extractedValue == null) {
            if (other.getExtractedValue() != null) return false;
        } else if (!extractedValue.equals(other.getExtractedValue())) {
            return false;
        }

        return true;
    }
}
