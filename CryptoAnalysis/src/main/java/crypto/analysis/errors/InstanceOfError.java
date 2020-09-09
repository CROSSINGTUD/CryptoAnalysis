package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLRule;

public class InstanceOfError extends ConstraintError {

	public InstanceOfError(CallSiteWithExtractedValue cs, CrySLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
		super(cs, rule, objectLocation, con);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return true;
	}
}
