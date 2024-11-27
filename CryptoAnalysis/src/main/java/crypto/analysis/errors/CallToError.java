package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Arrays;
import java.util.Collection;

public class CallToError extends AbstractError {

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
        return Arrays.hashCode(new Object[] {super.hashCode(), requiredMethods});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        CallToError other = (CallToError) obj;
        if (!super.equals(other)) return false;
        if (requiredMethods == null) {
            return other.getRequiredMethods() == null;
        } else {
            return requiredMethods.equals(other.getRequiredMethods());
        }
    }

    @Override
    public String toString() {
        return "CallToError: " + toErrorMarkerString();
    }
}
