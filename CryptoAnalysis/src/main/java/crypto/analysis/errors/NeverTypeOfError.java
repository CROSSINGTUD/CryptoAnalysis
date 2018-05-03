package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public class NeverTypeOfError extends ConstraintError {

	public NeverTypeOfError(CallSiteWithExtractedValue cs, CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con) {
		super(cs, rule, objectLocation, con);
	}

}
