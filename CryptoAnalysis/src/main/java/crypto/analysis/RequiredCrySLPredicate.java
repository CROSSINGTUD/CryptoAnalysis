package crypto.analysis;

import java.util.Set;

import boomerang.scene.Statement;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLPredicate;

public class RequiredCrySLPredicate implements ISLConstraint {

	private final CrySLPredicate predicate;
	private final Statement statement;

	public RequiredCrySLPredicate(CrySLPredicate predicate, Statement statement) {
		this.predicate = predicate;
		this.statement = statement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((statement == null) ? 0 : statement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequiredCrySLPredicate other = (RequiredCrySLPredicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (statement == null) {
            return other.statement == null;
		} else return statement.equals(other.statement);
    }

	public CrySLPredicate getPred() {
		return predicate;
	}

	public Statement getLocation() {
		return statement;
	}

	@Override
	public String toString() {
		return "misses " + predicate + " @ " + statement.toString();
	}

	@Override
	public String getName() {
		return predicate.getName();
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		return predicate.getInvolvedVarNames();
	}

}
