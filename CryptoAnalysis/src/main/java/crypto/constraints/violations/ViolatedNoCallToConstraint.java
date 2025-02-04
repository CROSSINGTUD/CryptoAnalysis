package crypto.constraints.violations;

import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;

/**
 * Represents a violation of the predefined predicate 'noCallTo[$methods]'
 *
 * @param method the methods that must not be called
 */
public record ViolatedNoCallToConstraint(CrySLMethod method) implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return "Call to " + CrySLUtils.formatMethodName(method) + " not allowed";
    }
}
