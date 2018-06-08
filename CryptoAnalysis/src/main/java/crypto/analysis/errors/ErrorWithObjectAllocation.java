package crypto.analysis.errors;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public abstract class ErrorWithObjectAllocation extends AbstractError{
	private final Node<Statement, Val> objectAllocationLocation;

	public ErrorWithObjectAllocation(Statement errorLocation, CryptSLRule rule, Node<Statement,Val> objectAllocationLocation) {
		super(errorLocation, rule);
		this.objectAllocationLocation = objectAllocationLocation;
	}

	public Node<Statement,Val> getObjectLocation(){
		return objectAllocationLocation;
	}

	protected String getObjectType() {
		if(this.objectAllocationLocation.fact() != null && this.objectAllocationLocation.fact().value() != null)
			return " on object of type " + this.objectAllocationLocation.fact().value().getType();
		return "";
	}
}
