package crypto.analysis.errors;

import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

/**
 * This class defines-IncompleteOperationError:
 *
 * <p>Found when the usage of an object may be incomplete. If there are multiple paths, and at least
 * one path introduces an incomplete operation, the analysis indicates that there is a potential
 * path with missing calls.
 *
 * <p>For example a Cipher object may be initialized but never been used for encryption or
 * decryption, this may render the code dead. This error heavily depends on the computed call graph
 * (CHA by default).
 */
public class IncompleteOperationError extends AbstractOrderError {

    private final Collection<CrySLMethod> expectedMethodCalls;
    private final boolean multiplePaths;

    /**
     * Create an IncompleteOperationError, if there is only one dataflow path, where the incomplete
     * operation occurs.
     *
     * @param seed the seed for the incomplete operation
     * @param errorStmt the statement of the last usage of the seed
     * @param rule the CrySL rule for the seed
     * @param expectedMethodsToBeCalled the methods that are expected to be called
     */
    public IncompleteOperationError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<CrySLMethod> expectedMethodsToBeCalled) {
        this(seed, errorStmt, rule, expectedMethodsToBeCalled, false);
    }

    /**
     * Create an IncompleteOperationError, if there is at least one dataflow path, where an
     * incomplete operation occurs.
     *
     * @param seed the seed for the incomplete operation
     * @param errorStmt the statement of the last usage of the seed
     * @param rule the CrySL rule for the seed
     * @param expectedMethodsToBeCalled the methods that are expected to be called
     * @param multiplePaths set to true, if there are multiple paths (default: false)
     */
    public IncompleteOperationError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<CrySLMethod> expectedMethodsToBeCalled,
            boolean multiplePaths) {
        super(seed, errorStmt, rule);

        this.expectedMethodCalls = expectedMethodsToBeCalled;
        this.multiplePaths = multiplePaths;
    }

    public Collection<CrySLMethod> getExpectedMethodCalls() {
        return expectedMethodCalls;
    }

    public boolean isMultiplePaths() {
        return multiplePaths;
    }

    @Override
    public String toErrorMarkerString() {
        if (multiplePaths) {
            return getErrorMarkerStringForMultipleDataflowPaths();
        } else {
            return getErrorMarkerStringForSingleDataflowPath();
        }
    }

    private String getErrorMarkerStringForSingleDataflowPath() {
        StringBuilder msg = new StringBuilder();
        msg.append("Operation");
        msg.append(getObjectType());
        msg.append(" not completed. Expected call to one of the methods ");

        String altMethods = CrySLUtils.formatMethodNames(expectedMethodCalls);
        msg.append(altMethods);
        return msg.toString();
    }

    private String getErrorMarkerStringForMultipleDataflowPaths() {
        Statement statement = getErrorStatement();
        if (!statement.containsInvokeExpr()) {
            return "Unable to describe error";
        }
        StringBuilder msg = new StringBuilder();
        msg.append("Call to ");

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        msg.append(invokeExpr.getMethod().getName());
        msg.append(getObjectType());
        msg.append(
                " is on a dataflow path with an incomplete operation. Potential missing call to one of the methods ");

        String altMethods = CrySLUtils.formatMethodNames(expectedMethodCalls);
        msg.append(altMethods);
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expectedMethodCalls, multiplePaths);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof IncompleteOperationError other
                && Objects.equals(expectedMethodCalls, other.getExpectedMethodCalls())
                && multiplePaths == other.isMultiplePaths();
    }

    @Override
    public String toString() {
        return "IncompleteOperationError: " + toErrorMarkerString();
    }
}
