package crypto.constraints.violations;

import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;
import java.util.Collection;

/**
 * Represents a violation of the predefined predicate 'callTo[$methods]'
 *
 * @param requiredMethods the methods that are expected to be called
 */
public record ViolatedCallToConstraint(Collection<CrySLMethod> requiredMethods)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return "Call to one of the methods "
                + CrySLUtils.formatMethodNames(requiredMethods)
                + " is missing";
    }
}
