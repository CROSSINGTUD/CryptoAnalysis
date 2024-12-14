package crypto.analysis.errors;

import boomerang.scene.Method;
import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractError {

    private final IAnalysisSeed seed;
    private final Statement errorStmt;
    private final CrySLRule rule;

    private final Collection<AbstractError> causedByErrors; // preceding
    private final Collection<AbstractError> willCauseErrors; // subsequent

    public AbstractError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule) {
        this.seed = seed;
        this.errorStmt = errorStmt;
        this.rule = rule;

        this.causedByErrors = new HashSet<>();
        this.willCauseErrors = new HashSet<>();
    }

    public abstract String toErrorMarkerString();

    public IAnalysisSeed getSeed() {
        return seed;
    }

    public Statement getErrorStatement() {
        return errorStmt;
    }

    public CrySLRule getRule() {
        return rule;
    }

    public Method getMethod() {
        return errorStmt.getMethod();
    }

    public int getLineNumber() {
        return errorStmt.getStartLineNumber();
    }

    public void addCausingError(AbstractError parent) {
        causedByErrors.add(parent);
    }

    public void addCausingError(Collection<AbstractError> parents) {
        causedByErrors.addAll(parents);
    }

    public void addSubsequentError(AbstractError subsequentError) {
        willCauseErrors.add(subsequentError);
    }

    public Collection<AbstractError> getSubsequentErrors() {
        return this.willCauseErrors;
    }

    public Collection<AbstractError> getRootErrors() {
        return this.causedByErrors;
    }

    public String toString() {
        return toErrorMarkerString();
    }

    protected String getObjectType() {
        return " on object of type " + seed.getFact().getType();
    }

    protected String formatMethodNames(Collection<CrySLMethod> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        for (CrySLMethod method : methods) {
            String formattedName = formatMethodName(method);
            builder.append(formattedName);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("}");

        return builder.toString();
    }

    protected String formatMethodName(CrySLMethod method) {
        StringBuilder builder = new StringBuilder();
        builder.append(method.getShortMethodName());
        builder.append("(");

        if (!method.getParameters().isEmpty()) {
            for (Map.Entry<String, String> param : method.getParameters()) {
                builder.append(param.getValue());
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed, errorStmt, rule);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractError other
                && Objects.equals(seed, other.seed)
                && Objects.equals(errorStmt, other.errorStmt)
                && Objects.equals(rule, other.rule);
    }
}
