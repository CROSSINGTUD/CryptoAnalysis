package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.WrappedClass;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;
import java.util.Arrays;

public class UncaughtExceptionError extends AbstractError {

    private final WrappedClass exception;

    public UncaughtExceptionError(
            IAnalysisSeed seed, Statement errorStmt, CrySLRule rule, WrappedClass exception) {
        super(seed, errorStmt, rule);
        this.exception = exception;
    }

    public WrappedClass getException() {
        return exception;
    }

    @Override
    public String toErrorMarkerString() {
        return String.format("Uncaught exception `%s`", exception.getName());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), exception});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        UncaughtExceptionError other = (UncaughtExceptionError) obj;
        if (exception == null) {
            if (other.getException() != null) return false;
        } else if (!exception.equals(other.getException())) {
            return false;
        }

        return true;
    }
}
