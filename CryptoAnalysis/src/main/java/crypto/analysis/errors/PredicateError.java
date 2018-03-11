package crypto.analysis.errors;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import crypto.analysis.LocatedCrySLPredicate;
import crypto.rules.CryptSLRule;
import crypto.typestate.CallSiteWithParamIndex;

public class PredicateError extends AbstractError{

	private LocatedCrySLPredicate contradictedPredicate;
	private Multimap<CallSiteWithParamIndex, Statement> extractedValues;

	public PredicateError(LocatedCrySLPredicate locatedCrySLPredicate, Statement statement, CryptSLRule cryptSLRule, Multimap<CallSiteWithParamIndex, Statement> extractedValues) {
		super(statement, cryptSLRule);
		this.contradictedPredicate = locatedCrySLPredicate;
		this.extractedValues = extractedValues;
	}
	public LocatedCrySLPredicate getContradictedPredicate() {
		return contradictedPredicate;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}

	public Multimap<CallSiteWithParamIndex, Statement> getExtractedValues() {
		return extractedValues;
	}

}
