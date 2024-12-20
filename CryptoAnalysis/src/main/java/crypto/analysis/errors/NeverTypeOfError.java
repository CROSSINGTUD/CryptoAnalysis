package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import crysl.rule.ISLConstraint;
import java.util.Objects;

public class NeverTypeOfError extends AbstractConstraintsError {

    private final CallSiteWithExtractedValue extractedValue;
    private final CrySLPredicate violatedConstraint;

    public NeverTypeOfError(
            IAnalysisSeed seed,
            CallSiteWithExtractedValue cs,
            CrySLRule rule,
            CrySLPredicate constraint) {
        super(seed, cs.callSiteWithParam().statement(), rule);

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
        return extractedValue.toString()
                + " should never be type of "
                + ((CrySLObject) violatedConstraint.getParameters().get(0)).getJavaType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extractedValue, violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof NeverTypeOfError other
                && Objects.equals(extractedValue, other.getExtractedValue())
                && Objects.equals(violatedConstraint, other.getViolatedConstraint());
    }

    @Override
    public String toString() {
        return "NeverTypeOfError: " + toErrorMarkerString();
    }
}
