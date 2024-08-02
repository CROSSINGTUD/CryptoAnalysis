package crypto.analysis;

import com.google.common.collect.Multimap;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.rules.CrySLPredicate;

import java.util.Collection;

public class EnsuredCrySLPredicate {

	private final CrySLPredicate predicate;
	private final Collection<CallSiteWithExtractedValue> parametersToValues;

	public EnsuredCrySLPredicate(CrySLPredicate predicate, Collection<CallSiteWithExtractedValue> parametersToValues) {
		this.predicate = predicate;
		this.parametersToValues = parametersToValues;
	}
	
	public CrySLPredicate getPredicate(){
		return predicate;
	}
	

	public Collection<CallSiteWithExtractedValue> getParametersToValues() {
		return  parametersToValues;
	}
	
	public String toString() {
		return "Proved " + predicate.getPredName(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnsuredCrySLPredicate other = (EnsuredCrySLPredicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

}
