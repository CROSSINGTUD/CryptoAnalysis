package crypto.analysis;

import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CrySLPredicate;

public class EnsuredCrySLPredicate {

	private final CrySLPredicate predicate;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues;
	private IAnalysisSeed[] seedsForParameters;

	public EnsuredCrySLPredicate(CrySLPredicate predicate, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues2) {
		this.predicate = predicate;
		parametersToValues = parametersToValues2;
		seedsForParameters = new IAnalysisSeed[predicate.getParameters().size()];
	}
	
	public void addAnalysisSeedToParameter(IAnalysisSeed seed, int paramPosition) {
		if(paramPosition < predicate.getParameters().size()) {
			seedsForParameters[paramPosition] = seed; 
		}
	}
	
	public IAnalysisSeed[] getParameterToAnalysisSeed() {
		return seedsForParameters;
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
		
		for(Entry<CallSiteWithParamIndex, ExtractedValue> e: parametersToValues.entries()) {
			result = prime * result + e.getValue().hashCode();
		}
		for(IAnalysisSeed seed: seedsForParameters) {
			result = prime * result + ((seed != null) ? seed.hashCode() : 0);
		}
		 
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
