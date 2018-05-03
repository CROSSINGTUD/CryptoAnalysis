package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;

public class RequiredPredicateError extends AbstractError{

	private CryptSLPredicate contradictedPredicate;
	private CallSiteWithExtractedValue extractedValues;

	public RequiredPredicateError(CryptSLPredicate contradictedPredicate, Statement location, CryptSLRule rule, CallSiteWithExtractedValue multimap) {
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

	public CallSiteWithExtractedValue getExtractedValues() {
		return extractedValues;
	}
}
