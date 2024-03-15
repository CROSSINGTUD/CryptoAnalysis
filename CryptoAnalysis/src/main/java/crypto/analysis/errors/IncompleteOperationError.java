package crypto.analysis.errors;

import boomerang.jimple.Statement;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/** <p>This class defines-IncompleteOperationError:</p>
 *
 * <p>Found when the usage of an object may be incomplete. If there are
 * multiple paths, and at least one path introduces an incomplete operation,
 * the analysis indicates that there is a potential path with missing calls.</p>
 *
 * <p>For example a Cipher object may be initialized but never been used for encryption or decryption, this may render the code dead.
 * This error heavily depends on the computed call graph (CHA by default).</p>
 */
public class IncompleteOperationError extends ErrorWithObjectAllocation{

	private final Collection<SootMethod> expectedMethodCalls;
	private final Set<String> expectedMethodCallsSet;
	private final boolean multiplePaths;

	/**
	 * Create an IncompleteOperationError, if there is only one dataflow path, where the
	 * incomplete operation occurs.
	 *
	 * @param objectLocation the seed for the incomplete operation
	 * @param errorLocation the statement of the last usage of the seed
	 * @param rule the CrySL rule for the seed
	 * @param expectedMethodsToBeCalled the methods that are expected to be called
	 */
	public IncompleteOperationError(IAnalysisSeed objectLocation, Statement errorLocation, CrySLRule rule, Collection<SootMethod> expectedMethodsToBeCalled) {
		this(objectLocation, errorLocation, rule, expectedMethodsToBeCalled, false);
	}

	/**
	 * Create an IncompleteOperationError, if there is at least one dataflow path, where an
	 * incomplete operation occurs.
	 *
	 * @param objectLocation the seed for the incomplete operation
	 * @param errorLocation the statement of the last usage of the seed
	 * @param rule the CrySL rule for the seed
	 * @param expectedMethodsToBeCalled the methods that are expected to be called
	 * @param multiplePaths set to true, if there are multiple paths (default: false)
	 */
	public IncompleteOperationError(IAnalysisSeed objectLocation, Statement errorLocation, CrySLRule rule, Collection<SootMethod> expectedMethodsToBeCalled, boolean multiplePaths) {
		super(errorLocation, rule, objectLocation);
		this.expectedMethodCalls = expectedMethodsToBeCalled;
		this.multiplePaths = multiplePaths;

		this.expectedMethodCallsSet = new HashSet<>();
		for (SootMethod method : expectedMethodCalls) {
			this.expectedMethodCallsSet.add(method.getSignature());
		}
	}

	public Collection<SootMethod> getExpectedMethodCalls() {
		return expectedMethodCalls;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
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
		msg.append(" not completed. Expected call to ");

		Set<String> altMethods = getFormattedExpectedCalls();
		msg.append(Joiner.on(", ").join(altMethods));
		return msg.toString();
	}

	private String getErrorMarkerStringForMultipleDataflowPaths() {
		if (!getErrorLocation().isCallsite() || !getErrorLocation().getUnit().isPresent()) {
			return "Unable to describe error";
		}
		StringBuilder msg = new StringBuilder();
		msg.append("Call to ");

		InvokeExpr invokeExpr = getErrorLocation().getUnit().get().getInvokeExpr();
		msg.append(invokeExpr.getMethod().getName());
		msg.append(getObjectType());
		msg.append(" is on a dataflow path with an incomplete operation. Potential missing call to ");

		Set<String> altMethods = getFormattedExpectedCalls();
		msg.append(Joiner.on(", ").join(altMethods));
		return msg.toString();
	}

	private Set<String> getFormattedExpectedCalls() {
		Set<String> altMethods = new HashSet<>();

		for (SootMethod expectedCall : getExpectedMethodCalls()) {
			if (stmtInvokesExpectedCallName(expectedCall.getName())){
				altMethods.add(expectedCall.getSignature().replace("<", "").replace(">", ""));
			} else {
				altMethods.add(expectedCall.getName().replace("<", "").replace(">", ""));
			}
		}
		return altMethods;
	}

	/**
	 * This method checks whether the statement at which the error is located already calls a method with the same name.
	 * This occurs when a call to the method with the correct name, but wrong signature is invoked.
	 */
	private boolean stmtInvokesExpectedCallName(String expectedCallName){
		Statement errorLocation = getErrorLocation();

		if (!errorLocation.isCallsite()) {
			return false;
		}

		Optional<Stmt> stmtOptional = errorLocation.getUnit().toJavaUtil();

		if (!stmtOptional.isPresent()) {
			return false;
		}

		Stmt stmt = stmtOptional.get();
		if (stmt.containsInvokeExpr()) {
			InvokeExpr call = stmt.getInvokeExpr();
			SootMethod calledMethod = call.getMethod();
			return calledMethod.getName().equals(expectedCallName);
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expectedMethodCallsSet == null) ? 0 : expectedMethodCallsSet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncompleteOperationError other = (IncompleteOperationError) obj;
		if (expectedMethodCalls == null) {
			if (other.expectedMethodCalls != null)
				return false;
		} else if (expectedMethodCallsSet != other.expectedMethodCallsSet)
			return false;
		
		return true;
	}
	
	@SuppressWarnings("unused")
	private int expectedMethodCallsHashCode(Collection<SootMethod> expectedMethodCalls) {		
		Set<String> expectedMethodCallsSet = Sets.newHashSet();
		for (SootMethod method : expectedMethodCalls) {
			expectedMethodCallsSet.add(method.getSignature());
		}
		return expectedMethodCallsSet.hashCode();
	}
	
	
}
