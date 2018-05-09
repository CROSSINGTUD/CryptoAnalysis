package crypto.analysis.errors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLRule;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;

public class IncompleteOperationError extends ErrorAtCodeObjectLocation{

	private Val errorVariable;
	private Collection<SootMethod> expectedMethodCalls;

	public IncompleteOperationError(Statement errorLocation,
			Val errorVariable, CryptSLRule rule, Node<Statement,Val> objectLocation, Collection<SootMethod> expectedMethodsToBeCalled) {
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
		Val errorVariable = getErrorVariable();
		Collection<SootMethod> expectedCalls = getExpectedMethodCalls();
		final StringBuilder msg = new StringBuilder();
		msg.append("Operation with ");
		final String type = errorVariable.value().getType().toString();
		msg.append(type.substring(type.lastIndexOf('.') + 1));
		msg.append(" object not completed. Expected call to ");
		final Set<String> altMethods = new HashSet<>();
		for (final SootMethod expectedCall : expectedCalls) {
			altMethods.add(expectedCall.getName());
		}
		msg.append(Joiner.on(", ").join(altMethods));
		return msg.toString();
	}
}
