package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractedValue;
import crysl.rule.CrySLRule;
import crysl.rule.ISLConstraint;
import java.util.Objects;

public class HardCodedError extends AbstractConstraintsError {

    private CallSiteWithExtractedValue extractedValue;
    private ISLConstraint violatedConstraint;

    public HardCodedError(
            IAnalysisSeed seed,
            CallSiteWithExtractedValue cs,
            CrySLRule rule,
            ISLConstraint constraint) {
        super(seed, cs.callSiteWithParam().statement(), rule);

        this.extractedValue = cs;
        this.violatedConstraint = constraint;
    }

    public HardCodedError(
            IAnalysisSeed seed,
            Statement statement,
            CrySLRule rule,
            Val val,
            int paramIndex,
            ExtractedValue extractedValue) {
        super(seed, statement, rule);
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
        return Objects.hash(super.hashCode(), extractedValue, violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof HardCodedError other
                && Objects.equals(extractedValue, other.getExtractedValue())
                && Objects.equals(violatedConstraint, other.getViolatedConstraint());
    }

    @Override
    public String toString() {
        return "HardCodedError: " + toErrorMarkerString();
    }
}
