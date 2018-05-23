package crypto.analysis;

import com.google.common.collect.Multimap;

import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CryptSLPredicate;

public class EnsuredCryptSLPredicate {

	private final CryptSLPredicate predicate;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues;

	public EnsuredCryptSLPredicate(CryptSLPredicate predicate, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues2) {
		this.predicate = predicate;
		parametersToValues = parametersToValues2;
	}
	
	public CryptSLPredicate getPredicate(){
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
		EnsuredCryptSLPredicate other = (EnsuredCryptSLPredicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

}
