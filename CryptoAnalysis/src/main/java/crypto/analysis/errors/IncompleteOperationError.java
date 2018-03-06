package crypto.analysis.errors;

import java.util.Collection;
import java.util.Set;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLMethod;
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
}
