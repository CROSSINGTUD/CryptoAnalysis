package crypto.analysis.errors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;

import boomerang.jimple.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
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
		Statement location = getErrorLocation();
		Collection<SootMethod> expectedCalls = getExpectedMethodCalls();
		final StringBuilder msg = new StringBuilder();
		msg.append("Unexpected call to method ");
		msg.append(getCalledMethodName(location));
		msg.append(getObjectType());
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedCalls) {
			altMethods.add(expectedCall.getName());
		}
		if(altMethods.isEmpty()){
			msg.append(".");
		} else{
			msg.append(". Expect a call to one of the following methods ");
			msg.append(Joiner.on(",").join(altMethods));
		}
		return msg.toString();
	}

	private String getCalledMethodName(Statement location) {
		Stmt stmt = location.getUnit().get();
		if(stmt.containsInvokeExpr()){
			return stmt.getInvokeExpr().getMethod().getName();
		}
		return stmt.toString();
	}
}
