package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;

public class RequiredPredicateError extends AbstractError{

	private CryptSLPredicate contradictedPredicate;
	private Multimap<CallSiteWithParamIndex, ExtractedValue> extractedValues;

	public RequiredPredicateError(CryptSLPredicate contradictedPredicate, Statement location, CryptSLRule rule, Multimap<CallSiteWithParamIndex, ExtractedValue> multimap) {
		super(location, rule);
		this.contradictedPredicate = contradictedPredicate;
		this.extractedValues = multimap;
	}

	public CryptSLPredicate getContradictedPredicate() {
		return contradictedPredicate;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}

	public Multimap<CallSiteWithParamIndex, ExtractedValue> getExtractedValues() {
		return extractedValues;
	}
}
