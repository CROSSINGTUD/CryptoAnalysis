package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

public class CallToError extends AbstractConstraintsError {

    private final Collection<CrySLMethod> requiredMethods;

    public CallToError(
            IAnalysisSeed seed, CrySLRule rule, Collection<CrySLMethod> requiredMethods) {
        super(seed, seed.getOrigin(), rule);

        this.requiredMethods = requiredMethods;
    }

    public Collection<CrySLMethod> getRequiredMethods() {
        return requiredMethods;
    }

    @Override
    public String toErrorMarkerString() {
        return "Call to one of the methods "
                + formatMethodNames(requiredMethods)
                + getObjectType()
                + " is missing";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requiredMethods);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof CallToError other
                && Objects.equals(requiredMethods, other.getRequiredMethods());
    }

    @Override
    public String toString() {
        return "CallToError: " + toErrorMarkerString();
    }
}
