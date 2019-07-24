package crypto.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;

public class AlternativeReqPredicate implements ISLConstraint {

	private static final long serialVersionUID = 9111353268603202392L;
	private final List<CryptSLPredicate> alternatives;
	private Statement stmt;

	public AlternativeReqPredicate(CryptSLPredicate alternativeOne,  Statement stmt) {
		this.alternatives = new ArrayList<CryptSLPredicate>();
		this.alternatives.add(alternativeOne);
		this.stmt = stmt;	
	}
	
	public AlternativeReqPredicate(CryptSLPredicate alternativeOne, CryptSLPredicate alternativeTwo, Statement stmt) {
		this.alternatives = new ArrayList<CryptSLPredicate>();
		this.alternatives.add(alternativeOne);
		this.alternatives.add(alternativeTwo);
		this.stmt = stmt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alternatives == null) ? 0 : alternatives.hashCode());
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
		if (alternatives == null) {
			if (other.alternatives != null)
				return false;
		} else if (!alternatives.equals(other.alternatives))
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
		return "misses " + alternatives.stream().map(e -> e.toString()).collect(Collectors.joining(" OR ")) + ((stmt != null) ? " @ " + stmt.toString() : "");
	}

	@Override
	public String getName() {
		return alternatives.stream().map(e -> e.getName()).collect(Collectors.joining(" OR "));
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> involvedVarNames = new HashSet<>();
		for (CryptSLPredicate alt : alternatives) {
			involvedVarNames.addAll(alt.getInvolvedVarNames());
		}
		return involvedVarNames;
	}

	@Override
	public void setLocation(Statement location) {
		throw new UnsupportedOperationException();
	}

	public List<CryptSLPredicate> getAlternatives() {
		return alternatives;
	}
	
	public boolean addAlternative(CryptSLPredicate newAlt) {
		return alternatives.add(newAlt);
	}

}
