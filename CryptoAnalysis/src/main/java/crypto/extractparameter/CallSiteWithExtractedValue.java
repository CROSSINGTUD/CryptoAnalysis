package crypto.extractparameter;

import boomerang.scene.Val;

public record CallSiteWithExtractedValue(
        CallSiteWithParamIndex callSiteWithParam, ExtractedValue extractedValue) {

    @Override
    public String toString() {
        String res;
        switch (callSiteWithParam.index()) {
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
                res = (callSiteWithParam.index() + 1) + "th ";
                break;
        }
        res += "parameter";
        if (extractedValue != null) {
            Val allocVal = extractedValue.val();

            if (allocVal.isConstant()) {
                res += " (with value " + allocVal.getVariableName() + ")";
            }
        }
        return res;
    }
}
