package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

import java.util.Arrays;
import java.util.Map.Entry;

public class PredicateContradictionError extends AbstractError {

	private final Entry<CrySLPredicate, CrySLPredicate> mismatchedPreds;

	public PredicateContradictionError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule, Entry<CrySLPredicate, CrySLPredicate> disPair) {
		super(seed, errorStmt, rule);

		this.mismatchedPreds = disPair;
	}

	public Entry<CrySLPredicate, CrySLPredicate> getMismatchedPreds() {
		return mismatchedPreds;
	}

	@Override
	public String toErrorMarkerString() {
		return "There is a predicate mismatch on the predicates " +
				mismatchedPreds.getKey() +
				" and " +
				mismatchedPreds.getValue();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{
				super.hashCode(),
				mismatchedPreds
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;

		PredicateContradictionError other = (PredicateContradictionError) obj;
		if (mismatchedPreds == null) {
			if (other.getMismatchedPreds() != null) return false;
		} else if (!mismatchedPreds.equals(other.getMismatchedPreds())) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "PredicateContradictionError: " + toErrorMarkerString();
	}

}
