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
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return true;
	}

}
