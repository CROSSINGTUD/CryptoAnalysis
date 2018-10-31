package crypto.analysis.errors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class IncompleteOperationError extends ErrorWithObjectAllocation{

	private Val errorVariable;
	private Collection<SootMethod> expectedMethodCalls;

	public IncompleteOperationError(Statement errorLocation,
			Val errorVariable, CryptSLRule rule, IAnalysisSeed objectLocation, Collection<SootMethod> expectedMethodsToBeCalled) {
		super(errorLocation, rule, objectLocation);
		this.errorVariable = errorVariable;
		this.expectedMethodCalls = expectedMethodsToBeCalled;
	}

	public Val getErrorVariable() {
		return errorVariable;
	}
	
	public Collection<SootMethod> getExpectedMethodCalls() {
		return expectedMethodCalls;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}

	@Override
	public String toErrorMarkerString() {
		Collection<SootMethod> expectedCalls = getExpectedMethodCalls();
		final StringBuilder msg = new StringBuilder();
		msg.append("Operation");
		msg.append(getObjectType());
		msg.append(" object not completed. Expected call to ");
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedCalls) {
			if (stmtInvokesExpectedCallName(expectedCall.getName())){
				altMethods.add(expectedCall.getSignature());
			} else {
				altMethods.add(expectedCall.getName());
			}
		}
		msg.append(Joiner.on(", ").join(altMethods));
		return msg.toString();
	}

	/**
	 * This method checks whether the statement at which the error is located already calls a method with the same name.
	 * This occurs when a call to the method with the correct name, but wrong signature is invoked.
	 */
	private boolean stmtInvokesExpectedCallName(String expectedCallName){
		Statement errorLocation = getErrorLocation();
		if (errorLocation.isCallsite()){
			Optional<Stmt> stmtOptional = errorLocation.getUnit();
			if (stmtOptional.isPresent()){
				Stmt stmt = stmtOptional.get();
				if (stmt.containsInvokeExpr()){
					InvokeExpr call = stmt.getInvokeExpr();
					SootMethod calledMethod = call.getMethod();
					if (calledMethod.getName().equals(expectedCallName)){
						return true;
					}
				}
			}
		}
		return false;
	}
}
