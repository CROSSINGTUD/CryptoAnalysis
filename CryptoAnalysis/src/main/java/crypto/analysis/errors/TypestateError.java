package crypto.analysis.errors;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import crypto.utils.MatcherUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TypestateError extends ErrorWithObjectAllocation{

	private final Collection<CrySLMethod> expectedMethodCalls;
	private final Set<String> expectedMethodCallsSet = Sets.newHashSet();

	public TypestateError(Statement errorStmt, CrySLRule rule, IAnalysisSeed object, Collection<CrySLMethod> expectedMethodCalls) {
		super(errorStmt, rule, object);
		this.expectedMethodCalls = expectedMethodCalls;
		
		for (CrySLMethod method : expectedMethodCalls) {
			this.expectedMethodCallsSet.add(method.getName());
		}	
	}

	// TODO Replace with callSet
	public Collection<CrySLMethod> getExpectedMethodCalls() {
		return expectedMethodCalls;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toErrorMarkerString() {
		final StringBuilder msg = new StringBuilder();
		boolean useSignatures = useSignatures();

		msg.append("Unexpected call to method ");
		msg.append(getCalledMethodString(useSignatures));
		msg.append(getObjectType());
		final Set<String> altMethods = new HashSet<>();
		for (CrySLMethod expectedCall : expectedMethodCalls) {
			// TODO Output
			if (useSignatures){
				altMethods.add(expectedCall.getName().replace("<", "").replace(">", ""));
			} else {
				altMethods.add(expectedCall.getName().replace("<", "").replace(">", ""));
			}
		}
		if(altMethods.isEmpty()){
			msg.append(".");
		} else{
			msg.append(". Expect a call to one of the following methods ");
			msg.append(Joiner.on(",").join(altMethods));
		}
		return msg.toString();
	}

	private String getCalledMethodString(boolean useSignature) {
		Statement stmt = getErrorStatement();

		if (stmt.containsInvokeExpr()) {
			if (useSignature) {
				return stmt.getInvokeExpr().getMethod().getSignature();
			} else {
				return stmt.getInvokeExpr().getMethod().getName();
			}
		}
		return stmt.toString();
	}

	/**
	 * This method checks whether the statement at which the error is located already calls a method with the same name
	 * as the expected method.
	 * This occurs when a call to the method with the correct name, but wrong signature is invoked.
	 */
	private boolean useSignatures(){
		Statement statement = getErrorStatement();

		if (statement.containsInvokeExpr()) {
			InvokeExpr call = statement.getInvokeExpr();
			DeclaredMethod calledMethod = call.getMethod();

			for (CrySLMethod expectedCall : getExpectedMethodCalls()){
				if (MatcherUtils.matchCryslMethodAndDeclaredMethod(expectedCall, calledMethod)){
					return true;
				}
			}
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
		TypestateError other = (TypestateError) obj;
		if (expectedMethodCallsSet == null) {
			if (other.expectedMethodCallsSet != null)
				return false;
		} else if (expectedMethodCallsSet != other.expectedMethodCallsSet)
			return false;
		return true;
	}
}
