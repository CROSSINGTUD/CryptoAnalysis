package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

import java.util.Map.Entry;

public class PredicateContradictionError extends AbstractError {

	Entry<CrySLPredicate, CrySLPredicate> mismatchedPreds;

	public PredicateContradictionError(Statement errorStmt, CrySLRule rule, Entry<CrySLPredicate, CrySLPredicate> disPair) {
		super(errorStmt, rule);
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
