package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLRule;

public class InstanceOfError extends ErrorWithObjectAllocation {

	public InstanceOfError(CallSiteWithExtractedValue cs, CrySLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
		super(cs.getCallSite().stmt(), rule, objectLocation);
	}

	@Override
	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toErrorMarkerString() {
		return "InstanceOf error";
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
