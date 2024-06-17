package crypto.analysis;

import boomerang.scene.Statement;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLPredicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlternativeReqPredicate implements ISLConstraint {

	private final Statement stmt;
	private final List<CrySLPredicate> alternatives;

	public AlternativeReqPredicate(CrySLPredicate alternativeOne, Statement stmt) {
		this.alternatives = new ArrayList<>();
		this.alternatives.add(alternativeOne);
		this.stmt = stmt;	
	}
	
	public AlternativeReqPredicate(CrySLPredicate alternativeOne, CrySLPredicate alternativeTwo, Statement stmt) {
		this.alternatives = new ArrayList<>();
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
            return other.stmt == null;
		} else return stmt.equals(other.stmt);
    }

	public Statement getLocation() {
		return stmt;
	}

	@Override
	public String toString() {
		return "misses " + alternatives.stream().map(CrySLPredicate::toString).collect(Collectors.joining(" OR ")) + ((stmt != null) ? " @ " + stmt : "");
	}

	@Override
	public String getName() {
		return alternatives.stream().map(CrySLPredicate::getName).collect(Collectors.joining(" OR "));
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> involvedVarNames = new HashSet<>();
		for (CrySLPredicate alt : alternatives) {
			involvedVarNames.addAll(alt.getInvolvedVarNames());
		}
		return involvedVarNames;
	}

    public List<CrySLPredicate> getAlternatives() {
		return alternatives;
	}
	
	public boolean addAlternative(CrySLPredicate newAlt) {
		return alternatives.add(newAlt);
	}

}
