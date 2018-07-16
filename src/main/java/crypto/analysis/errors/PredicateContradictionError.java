package crypto.analysis.errors;

import java.util.Map.Entry;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;

public class PredicateContradictionError extends AbstractError {

	Entry<CryptSLPredicate, CryptSLPredicate> mismatchedPreds;

	public PredicateContradictionError(Statement errorLocation, CryptSLRule rule, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
		super(errorLocation, rule);
		mismatchedPreds = disPair;
	}

	@Override
	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toErrorMarkerString() {
		return "Predicate mismatch";
	}

	public Entry<CryptSLPredicate, CryptSLPredicate> getMismatchedPreds() {
		return mismatchedPreds;
	}

}
