package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;

public class HardCodedError extends ConstraintError {

	public HardCodedError(CallSiteWithExtractedValue cs, CryptSLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
		super(cs, rule, objectLocation, con);
	}

}
