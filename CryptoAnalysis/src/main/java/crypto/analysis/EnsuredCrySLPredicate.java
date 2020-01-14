package crypto.analysis;

import com.google.common.collect.Multimap;

import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CrySLPredicate;

public class EnsuredCrySLPredicate {

	private final CrySLPredicate predicate;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues;

	public EnsuredCrySLPredicate(CrySLPredicate predicate, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues2) {
		this.predicate = predicate;
		parametersToValues = parametersToValues2;
	}
	
	public CrySLPredicate getPredicate(){
		return predicate;
	}
	

	public Multimap<CallSiteWithParamIndex, ExtractedValue> getParametersToValues() {
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
