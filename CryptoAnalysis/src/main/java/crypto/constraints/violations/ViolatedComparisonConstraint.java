package crypto.constraints.violations;

import crypto.constraints.ComparisonConstraint;

/**
 * Represents a violated {@link ComparisonConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedComparisonConstraint(ComparisonConstraint constraint)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        // TODO Create a detailed error message
        return "";
    }
}
