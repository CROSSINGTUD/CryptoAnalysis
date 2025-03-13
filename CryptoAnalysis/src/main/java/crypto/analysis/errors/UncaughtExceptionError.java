package crypto.analysis.errors;

import boomerang.scope.Statement;
import boomerang.scope.WrappedClass;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import java.util.Objects;

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
        return String.format("Uncaught exception `%s`", exception.getFullyQualifiedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exception);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof UncaughtExceptionError other
                && Objects.equals(exception, other.getException());
    }
}
