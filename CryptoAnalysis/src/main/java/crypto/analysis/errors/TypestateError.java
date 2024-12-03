package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Arrays;
import java.util.Collection;

public class TypestateError extends AbstractError {

    private final Collection<CrySLMethod> expectedMethodCalls;

    public TypestateError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<CrySLMethod> expectedMethodCalls) {
        super(seed, errorStmt, rule);

        this.expectedMethodCalls = expectedMethodCalls;
    }

    public Collection<CrySLMethod> getExpectedMethodCalls() {
        return expectedMethodCalls;
    }

    @Override
    public String toErrorMarkerString() {
        final StringBuilder msg = new StringBuilder();

        msg.append("Unexpected call to method ");
        msg.append(getErrorStatement().getInvokeExpr().getMethod());
        msg.append(getObjectType());

        String altMethods = formatMethodNames(expectedMethodCalls);

        if (!altMethods.isEmpty()) {
            msg.append(". Expected a call to one of the following methods ");
            msg.append(altMethods);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), expectedMethodCalls});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        TypestateError other = (TypestateError) obj;
        if (expectedMethodCalls == null) {
            if (other.getExpectedMethodCalls() != null) return false;
        } else if (!expectedMethodCalls.equals(other.getExpectedMethodCalls())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TypestateError: " + toErrorMarkerString();
    }
}
