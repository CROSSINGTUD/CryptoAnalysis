package crypto.analysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLPredicate;

public class EnsuredCryptSLPredicate {

	private final CryptSLPredicate predicate;
	private Multimap<String, String> parametersToValues = HashMultimap.create();

	public EnsuredCryptSLPredicate(CryptSLPredicate predicate, Multimap<String, String> analysisSeedWithSpecification) {
		this.predicate = predicate;
		parametersToValues = analysisSeedWithSpecification;
	}
	
	public CryptSLPredicate getPredicate(){
		return predicate;
	}
	

	public Multimap<String, String> getParametersToValues() {
		return  parametersToValues;
	}
	
	public String toString() {
		return "ENS ->" + predicate.getPredName() + " on " + predicate.getInvolvedVarNames().get(0); 
	}

}
