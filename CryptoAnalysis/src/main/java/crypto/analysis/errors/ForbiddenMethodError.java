package crypto.analysis.errors;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ForbiddenMethodError extends AbstractError {

    private final DeclaredMethod calledMethod;
    private final Collection<CrySLMethod> alternatives;

    public ForbiddenMethodError(
            IAnalysisSeed seed,
            Statement errorLocation,
            CrySLRule rule,
            DeclaredMethod calledMethod) {
        this(seed, errorLocation, rule, calledMethod, new HashSet<>());
    }

    public ForbiddenMethodError(
            IAnalysisSeed seed,
            Statement errorLocation,
            CrySLRule rule,
            DeclaredMethod calledMethod,
            Collection<CrySLMethod> alternatives) {
        super(seed, errorLocation, rule);

        this.calledMethod = calledMethod;
        this.alternatives = alternatives;
    }

    public Collection<CrySLMethod> getAlternatives() {
        return alternatives;
    }

    public DeclaredMethod getCalledMethod() {
        return calledMethod;
    }

    @Override
    public String toErrorMarkerString() {
        final StringBuilder msg = new StringBuilder();
        msg.append("Detected call to forbidden method ");
        msg.append(getCalledMethod().getSignature());
        msg.append(" of class ");
        msg.append(getCalledMethod().getDeclaringClass());

        if (!getAlternatives().isEmpty()) {
            msg.append(". Instead, call one of the methods ");
            String altMethods = formatMethodNames(alternatives);
            msg.append(altMethods);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), calledMethod, alternatives});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        ForbiddenMethodError other = (ForbiddenMethodError) obj;
        if (calledMethod == null) {
            if (other.getCalledMethod() != null) return false;
        } else if (!calledMethod.equals(other.getCalledMethod())) {
            return false;
        }

        if (alternatives == null) {
            if (other.getAlternatives() != null) return false;
        } else if (!alternatives.equals(other.getAlternatives())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ForbiddenMethodError: " + toErrorMarkerString();
    }
}
