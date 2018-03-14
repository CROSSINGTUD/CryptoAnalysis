package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;
import crypto.typestate.CallSiteWithParamIndex;
import sync.pds.solver.nodes.Node;

public class ConstraintError extends ErrorAtCodeObjectLocation{

	private ISLConstraint brokenConstraint;
	private Multimap<CallSiteWithParamIndex, Statement> extractedValues;

	public ConstraintError(Statement stmt, CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con, Multimap<CallSiteWithParamIndex, Statement> extractedValues) {
		super(stmt, rule, objectLocation);
		this.brokenConstraint = con;
		this.extractedValues = extractedValues;
	}
	
	public ISLConstraint getBrokenConstraint() {
		return brokenConstraint;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}

	public Multimap<CallSiteWithParamIndex, Statement> getExtractedValues() {
		return extractedValues;
	}

}
