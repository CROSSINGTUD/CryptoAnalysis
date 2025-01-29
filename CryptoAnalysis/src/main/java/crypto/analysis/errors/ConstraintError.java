package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.constraints.violations.IViolatedConstraint;
import crysl.rule.CrySLRule;
import java.util.Objects;

/** Represents an error for a violated constraint from the CONSTRAINTS section */
public class ConstraintError extends AbstractConstraintsError {

    private final EvaluableConstraint evaluableConstraint;
    private final IViolatedConstraint violatedConstraint;

    /**
     * Constructs a ConstraintError for a violated constraint
     *
     * @param seed the seed with the violated predicate
     * @param statement the statement of the violation
     * @param rule the rule containing the violated predicate
     * @param evaluableConstraint the evaluated constraint
     * @param violatedConstraint the violated constraint
     */
    public ConstraintError(
            IAnalysisSeed seed,
            Statement statement,
            CrySLRule rule,
            EvaluableConstraint evaluableConstraint,
            IViolatedConstraint violatedConstraint) {
        super(seed, statement, rule);

        this.evaluableConstraint = evaluableConstraint;
        this.violatedConstraint = violatedConstraint;
    }

    public IViolatedConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        return "Constraint \""
                + evaluableConstraint
                + "\" on object "
                + getSeed().getFact().getVariableName()
                + " is violated due to the following reason: "
                + violatedConstraint.getErrorMessage();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ConstraintError other
                && Objects.equals(violatedConstraint, other.violatedConstraint);
    }

    @Override
    public String toString() {
        return "ConstraintError: " + toErrorMarkerString();
    }
}
