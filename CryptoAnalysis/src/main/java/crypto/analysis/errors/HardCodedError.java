package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLRule;

public class HardCodedError extends ConstraintError {

	public HardCodedError(CallSiteWithExtractedValue cs, CrySLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
		super(cs, rule, objectLocation, con);
	}

}
