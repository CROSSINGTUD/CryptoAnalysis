package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLRule;
import crypto.rules.ISLConstraint;
import java.util.Arrays;

public class HardCodedError extends AbstractError {

    private final CallSiteWithExtractedValue extractedValue;
    private final ISLConstraint violatedConstraint;

    public HardCodedError(
            IAnalysisSeed seed,
            CallSiteWithExtractedValue cs,
            CrySLRule rule,
            ISLConstraint constraint) {
        super(seed, cs.getCallSiteWithParam().stmt(), rule);

        this.extractedValue = cs;
        this.violatedConstraint = constraint;
    }

    public CallSiteWithExtractedValue getExtractedValue() {
        return extractedValue;
    }

    public ISLConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        return extractedValue.toString() + " should never be hard coded";
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), extractedValue, violatedConstraint});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        HardCodedError other = (HardCodedError) obj;
        if (extractedValue == null) {
            if (other.getExtractedValue() != null) return false;
        } else if (!extractedValue.equals(other.getExtractedValue())) {
            return false;
        }

        if (violatedConstraint == null) {
            if (other.getViolatedConstraint() != null) return false;
        } else if (!violatedConstraint.equals(other.getViolatedConstraint())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "HardCodedError: " + toErrorMarkerString();
    }
}
