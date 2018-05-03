package crypto.analysis.errors;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public class ConstraintError extends ErrorAtCodeObjectLocation{

	private ISLConstraint brokenConstraint;
	private CallSiteWithExtractedValue callSiteWithParamIndex;

	public ConstraintError(CallSiteWithExtractedValue cs,  CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con) {
		super(cs.getCallSite().stmt(), rule, objectLocation);
		this.callSiteWithParamIndex = cs;
		this.brokenConstraint = con;
	}
	
	public ISLConstraint getBrokenConstraint() {
		return brokenConstraint;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}


	public CallSiteWithExtractedValue getCallSiteWithExtractedValue() {
		return callSiteWithParamIndex;
	}
}
