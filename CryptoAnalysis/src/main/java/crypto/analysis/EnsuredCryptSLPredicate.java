package crypto.analysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLPredicate;
import ideal.IFactAtStatement;

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
	//encrypted(byte[] ciphertext, byte[] plaintext, String alg("AES"))
}
