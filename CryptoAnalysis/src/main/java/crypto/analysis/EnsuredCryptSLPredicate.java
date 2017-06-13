package crypto.analysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLPredicate;

public class EnsuredCryptSLPredicate {

	private CryptSLPredicate predicate;
	private Multimap<String, String> parametersToValues = HashMultimap.create();

	public EnsuredCryptSLPredicate(CryptSLPredicate predicate, Multimap<String, String> collectedValues) {
		this.predicate = predicate;
		parametersToValues = collectedValues;
	}
	
	public CryptSLPredicate getPredicate(){
		return predicate;
	}

	public Multimap<String, String> getParametersToValues() {
		return  HashMultimap.create(parametersToValues);
	}
	
}
