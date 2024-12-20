package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import crysl.rule.ISLConstraint;
import java.util.Objects;

public class ImpreciseValueExtractionError extends AbstractConstraintsError {

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
        return Objects.hash(super.hashCode(), violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ImpreciseValueExtractionError other
                && Objects.equals(violatedConstraint, other.getViolatedConstraint());
    }

    @Override
    public String toString() {
        return "ImpreciseValueExtractionError: " + toErrorMarkerString();
    }
}
