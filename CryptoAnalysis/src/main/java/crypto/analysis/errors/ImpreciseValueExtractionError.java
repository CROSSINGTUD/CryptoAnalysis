package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ExtractedValue;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class ImpreciseValueExtractionError extends AbstractConstraintsError {

    private final EvaluableConstraint violatedConstraint;

    public ImpreciseValueExtractionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            EvaluableConstraint constraint) {
        super(seed, errorStmt, rule);
        this.violatedConstraint = constraint;
    }

    public ImpreciseValueExtractionError(
            AnalysisSeedWithSpecification seed,
            Statement statement,
            CrySLRule rule,
            ParameterWithExtractedValues parameter,
            ExtractedValue value,
            EvaluableConstraint constraint) {
        super(seed, statement, rule);

        violatedConstraint = constraint;
    }

    public EvaluableConstraint getViolatedConstraint() {
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
