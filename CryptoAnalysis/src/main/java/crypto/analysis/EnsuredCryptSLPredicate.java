package crypto.analysis;

import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import soot.Value;

public class EnsuredCryptSLPredicate {

	private CryptSLPredicate predicate;
	private Multimap<String, Value> parametersToValues = HashMultimap.create();

	public EnsuredCryptSLPredicate(CryptSLPredicate predicate, Multimap<CallSiteWithParamIndex, Value> collectedValues) {
		this.predicate = predicate;
		for(Entry<CallSiteWithParamIndex, Value> e : collectedValues.entries()){
			if(predicate.getParameters().contains(e.getKey().getVarName())){
				parametersToValues.put(e.getKey().getVarName(), e.getValue());
			}
		}
	}
	
	public CryptSLPredicate getPredicate(){
		return predicate;
	}

	public Multimap<String, Value> getParametersToValues() {
		return  HashMultimap.create(parametersToValues);
	}
	
}
