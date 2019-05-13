package crypto.analysis.errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import boomerang.jimple.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class TypestateError extends ErrorWithObjectAllocation{

	private Collection<SootMethod> expectedMethodCalls;

	public TypestateError(Statement stmt, CryptSLRule rule, IAnalysisSeed object, Collection<SootMethod> expectedMethodCalls) {
		super(stmt, rule, object);
		this.expectedMethodCalls = expectedMethodCalls;
	}

	public Collection<SootMethod> getExpectedMethodCalls() {
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
		Statement location = getErrorLocation();
		msg.append(getCalledMethodString(location, useSignatures));
		msg.append(getObjectType());
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedMethodCalls) {
			if (useSignatures){
				altMethods.add(expectedCall.getSignature().replace("<", "").replace(">", ""));
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

	private String getCalledMethodString(Statement location, boolean useSignature) {
		Stmt stmt = location.getUnit().get();
		if(stmt.containsInvokeExpr()){
			if (useSignature){
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
		Statement errorLocation = getErrorLocation();
		if (errorLocation.isCallsite()){
			Optional<Stmt> stmtOptional = errorLocation.getUnit();
			if (stmtOptional.isPresent()){
				Stmt stmt = stmtOptional.get();
				if (stmt.containsInvokeExpr()){
					InvokeExpr call = stmt.getInvokeExpr();
					SootMethod calledMethod = call.getMethod();
					for (SootMethod expectedCall : getExpectedMethodCalls()){
						if (calledMethod.getName().equals(expectedCall.getName())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expectedMethodCalls == null) ? 0 :expectedMethodCallsHashCode(expectedMethodCalls));
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
		if (expectedMethodCalls == null) {
			if (other.expectedMethodCalls != null)
				return false;
		} else if (expectedMethodCallsHashCode(expectedMethodCalls) != (expectedMethodCallsHashCode(other.expectedMethodCalls)))
			return false;
		return true;
	}

	private int expectedMethodCallsHashCode(Collection<SootMethod> expectedMethodCalls) {
		final int prime = 31;
		int result = 1;
		
		List<SootMethod> expectedMethodCallsList = new ArrayList<SootMethod>(expectedMethodCalls);
		Collections.sort(expectedMethodCallsList, new Comparator<SootMethod>() {
			public int compare(SootMethod s1, SootMethod s2) {
				return s1.getSignature().toString().compareToIgnoreCase(s2.getSignature().toString());
			}
		});

		for (SootMethod method : expectedMethodCallsList) {
			result = prime * result + ((method == null) ? 0 : method.getSignature().toString().hashCode());
		}
		return result;
	}
}
