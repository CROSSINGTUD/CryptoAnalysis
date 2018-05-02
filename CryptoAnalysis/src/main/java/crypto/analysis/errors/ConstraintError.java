package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public class ConstraintError extends ErrorAtCodeObjectLocation{

	private ISLConstraint brokenConstraint;
	private Multimap<CallSiteWithParamIndex, ExtractedValue> extractedValues;

	public ConstraintError(Statement stmt,  CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con, Multimap<CallSiteWithParamIndex, ExtractedValue> extractedValues) {
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

	public Multimap<CallSiteWithParamIndex, ExtractedValue> getExtractedValues() {
		return extractedValues;
	}

}
