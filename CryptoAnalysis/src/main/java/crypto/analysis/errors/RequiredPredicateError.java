package crypto.analysis.errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import boomerang.jimple.Statement;
import crypto.analysis.HiddenPredicate;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

/**
 * Creates {@link RequiredPredicateError} for all Required Predicate error generates RequiredPredicateError
 *
 *
 * contradictedPredicate a {@link CrySLPredicate} holds the contradicted required predicate or parameter
 * extractedValues a {@link CallSiteWithExtractedValue} hold the location value of the missing required predicate or parameter
 */

public class RequiredPredicateError extends AbstractError{

	private List<CrySLPredicate> contradictedPredicate;
	private CallSiteWithExtractedValue extractedValues;
	private List<HiddenPredicate> hiddenPredicates;

	public RequiredPredicateError(List<CrySLPredicate> contradictedPredicates, Statement location, CrySLRule rule, CallSiteWithExtractedValue multimap) {
		super(location, rule);
		this.contradictedPredicate = contradictedPredicates;
		this.extractedValues = multimap;
		this.hiddenPredicates = new ArrayList<>();
	}

	public void addHiddenPredicates(Collection<HiddenPredicate> hiddenPredicates) {
		this.hiddenPredicates.addAll(hiddenPredicates);
	}

	public void mapPrecedingErrors() {
		for (HiddenPredicate hiddenPredicate : hiddenPredicates) {
			Collection<AbstractError> precedingErrors = hiddenPredicate.getPrecedingErrors();
			this.addCausingError(precedingErrors);
			precedingErrors.forEach(e -> e.addSubsequentError(this));
		}
	}

	/**
	 * This method returns a list of contradicting predicates
	 * @return list of contradicting predicates
	 */
	public List<CrySLPredicate> getContradictedPredicates() {
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
		String predicateName = getContradictedPredicates().stream().map(e -> e.getPredName()).collect(Collectors.joining(" OR "));
		String[] parts = predicateName.split("(?=[A-Z])");
		msg += parts[0];
		for(int i=1; i<parts.length; i++)
			msg +=  parts[i];

		if (predicateName.equals("preparedIV") && extractedValues.toString().equals("Third parameter"))
		{
			msg += " [ with CBC, It's required to use IVParameterSpec]";
		}
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
