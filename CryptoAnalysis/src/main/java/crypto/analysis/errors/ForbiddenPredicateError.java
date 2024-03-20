package crypto.analysis.errors;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

public class ForbiddenPredicateError extends AbstractError{

	private CrySLPredicate contradictedPredicate;
	private CallSiteWithExtractedValue extractedValues;

	public ForbiddenPredicateError(CrySLPredicate contradictedPredicate, Statement errorStmt, CrySLRule rule, CallSiteWithExtractedValue multimap) {
		super(errorStmt, rule);
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
		msg += " has property ";
		msg += getContradictedPredicate().getPredName();
		return msg + " although it should not.";
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
		ForbiddenPredicateError other = (ForbiddenPredicateError) obj;
		if (contradictedPredicate == null) {
			if (other.contradictedPredicate != null)
				return false;
		} else if (!contradictedPredicate.equals(other.contradictedPredicate))
			return false;
		return true;
	}

}
