package crypto.analysis.errors;

import java.util.Map.Entry;

import boomerang.jimple.Statement;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

public class PredicateContradictionError extends AbstractError {

	Entry<CrySLPredicate, CrySLPredicate> mismatchedPreds;

	public PredicateContradictionError(Statement errorLocation, CrySLRule rule, Entry<CrySLPredicate, CrySLPredicate> disPair) {
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

	public Entry<CrySLPredicate, CrySLPredicate> getMismatchedPreds() {
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
