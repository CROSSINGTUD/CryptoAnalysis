package crypto.analysis.errors;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public abstract class ErrorAtCodeObjectLocation extends AbstractError{
	private Node<Statement, Val> objectLocation;

	public ErrorAtCodeObjectLocation(Statement errorLocation, CryptSLRule rule, Node<Statement,Val> objectLocation) {
		super(errorLocation, rule);
		this.objectLocation = objectLocation;
	}

	public Node<Statement,Val> getObjectLocation(){
		return objectLocation;
	}
}
