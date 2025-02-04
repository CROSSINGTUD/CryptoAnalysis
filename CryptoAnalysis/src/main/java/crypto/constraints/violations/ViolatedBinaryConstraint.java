package crypto.constraints.violations;

import crypto.constraints.BinaryConstraint;

/**
 * Represents the violation of a {@link BinaryConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedBinaryConstraint(BinaryConstraint constraint) implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        // TODO Create a detailed error message
        return "";
    }
}
