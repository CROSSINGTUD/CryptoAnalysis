package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;
import sync.pds.solver.nodes.Node;

public class NeverTypeOfError extends ConstraintError {

	public NeverTypeOfError(Statement stmt, CryptSLRule rule, Node<Statement, Val> objectLocation, ISLConstraint con,
			Multimap<CallSiteWithParamIndex, ExtractedValue> extractedValues) {
		super(stmt, rule, objectLocation, con, extractedValues);
	}

}
