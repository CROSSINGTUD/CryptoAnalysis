package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;
import crypto.rules.ISLConstraint;
import java.util.Arrays;

public class ImpreciseValueExtractionError extends AbstractError {

    private final ISLConstraint violatedConstraint;

    public ImpreciseValueExtractionError(
            IAnalysisSeed seed, Statement errorStmt, CrySLRule rule, ISLConstraint constraint) {
        super(seed, errorStmt, rule);
        this.violatedConstraint = constraint;
    }

    public ISLConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        return "Constraint "
                + violatedConstraint
                + " could not be evaluated due to insufficient information.";
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), violatedConstraint});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        ImpreciseValueExtractionError other = (ImpreciseValueExtractionError) obj;
        if (violatedConstraint == null) {
            if (other.violatedConstraint != null) return false;
        } else if (!violatedConstraint.equals(other.violatedConstraint)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ImpreciseValueExtractionError: " + toErrorMarkerString();
    }
}
