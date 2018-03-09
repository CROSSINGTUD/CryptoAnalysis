package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.analysis.LocatedCrySLPredicate;
import crypto.rules.CryptSLRule;

public class RequiredPredicateError extends AbstractError{

	private LocatedCrySLPredicate contradictedPredicate;

	public RequiredPredicateError(Statement stmt, CryptSLRule rule, LocatedCrySLPredicate contradictedPredicate) {
		super(stmt, rule);
		this.contradictedPredicate = contradictedPredicate;
	}
	public LocatedCrySLPredicate getContradictedPredicate() {
		return contradictedPredicate;
	}
	
	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}
}
