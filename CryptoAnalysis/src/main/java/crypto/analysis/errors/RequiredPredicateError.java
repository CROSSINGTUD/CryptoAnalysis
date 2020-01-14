package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

public class RequiredPredicateError extends AbstractError{

	private CrySLPredicate contradictedPredicate;
	private CallSiteWithExtractedValue extractedValues;

	public RequiredPredicateError(CrySLPredicate contradictedPredicate, Statement location, CrySLRule rule, CallSiteWithExtractedValue multimap) {
		super(location, rule);
		this.contradictedPredicate = contradictedPredicate;
		this.extractedValues = multimap;
	}

	public CrySLPredicate getContradictedPredicate() {
		return contradictedPredicate;
	}
	
	public CallSiteWithExtractedValue getExtractedValues() {
		return extractedValues;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}


	@Override
	public String toErrorMarkerString() {
		String msg = extractedValues.toString();
		msg += " was not properly generated as ";
		String predicateName = getContradictedPredicate().getPredName();
		String[] parts = predicateName.split("(?=[A-Z])");
		msg += parts[0];
		for(int i=1; i<parts.length; i++)
			msg += " " + parts[i];
		return msg;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contradictedPredicate == null) ? 0 : contradictedPredicate.hashCode());
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
		RequiredPredicateError other = (RequiredPredicateError) obj;
		if (contradictedPredicate == null) {
			if (other.contradictedPredicate != null)
				return false;
		} else if (!contradictedPredicate.equals(other.contradictedPredicate))
			return false;
		return true;
	}

}
