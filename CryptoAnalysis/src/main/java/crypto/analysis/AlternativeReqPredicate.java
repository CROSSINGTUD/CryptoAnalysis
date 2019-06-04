package crypto.analysis;

import java.util.Set;
import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;

public class AlternativeReqPredicate implements ISLConstraint {

	private static final long serialVersionUID = 9111353268603202392L;
	private final CryptSLPredicate alternative1;
	private final CryptSLPredicate alternative2;
	private final Statement stmt;

	public AlternativeReqPredicate(CryptSLPredicate alternativeOne, CryptSLPredicate alternativeTwo, Statement stmt) {
		this.alternative1 = alternativeOne;
		this.alternative2 = alternativeTwo;
		this.stmt = stmt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alternative1 == null) ? 0 : alternative1.hashCode());
		result = prime * result + ((alternative2 == null) ? 0 : alternative2.hashCode());
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
		AlternativeReqPredicate other = (AlternativeReqPredicate) obj;
		if (alternative1 == null) {
			if (other.alternative1 != null)
				return false;
		} else if (!alternative1.equals(other.alternative1))
			return false;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		return true;
	}

	public Statement getLocation() {
		return stmt;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "misses " + alternative1 + " OR " + alternative2 + " @ " + stmt.toString();
	}

	@Override
	public String getName() {
		return alternative1.getName() + alternative2.getName();
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> involvedVarNames = alternative1.getInvolvedVarNames();
		involvedVarNames.addAll(alternative2.getInvolvedVarNames());
		return involvedVarNames;
	}

	@Override
	public void setLocation(Statement location) {
		throw new UnsupportedOperationException();
	}

	public CryptSLPredicate getAlternative1() {
		return alternative1;
	}

	public CryptSLPredicate getAlternative2() {
		return alternative2;
	}
}
