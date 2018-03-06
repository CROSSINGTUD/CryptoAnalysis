package crypto.analysis.errors;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;
import typestate.interfaces.ISLConstraint;

public class ConstraintError extends ErrorAtCodeObjectLocation{

	private ISLConstraint brokenConstraint;

	public ConstraintError(Statement stmt, CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con) {
		super(stmt, rule, objectLocation);
		this.brokenConstraint = con;
	}
	
	public ISLConstraint getBrokenConstraint() {
		return brokenConstraint;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}
}
