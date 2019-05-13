package crypto.analysis;

import java.util.Set;
import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;

public class RequiredCryptSLPredicate implements ISLConstraint {

	private static final long serialVersionUID = 9111353268603202392L;
	private final CryptSLPredicate predicate;
	private final Statement stmt;

	public RequiredCryptSLPredicate(CryptSLPredicate predicate, Statement stmt) {
		this.predicate = predicate;
		this.stmt = stmt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
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
		RequiredCryptSLPredicate other = (RequiredCryptSLPredicate) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		return true;
	}

	public CryptSLPredicate getPred() {
		return predicate;
	}

	public Statement getLocation() {
		return stmt;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "misses " + predicate + " @ " + stmt.toString();
	}

	@Override
	public String getName() {
		return predicate.getName();
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		return predicate.getInvolvedVarNames();
	}

	@Override
	public void setLocation(Statement location) {
		throw new UnsupportedOperationException();
	}
}
