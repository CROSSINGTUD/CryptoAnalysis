package crypto.analysis;

import boomerang.scene.Statement;
import crypto.rules.CrySLPredicate;
import crypto.rules.ISLConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlternativeReqPredicate implements ISLConstraint {

    private final List<CrySLPredicate> alternatives;
    private final Statement stmt;
    private final int paramIndex;

    public AlternativeReqPredicate(CrySLPredicate alternativeOne, Statement stmt, int paramIndex) {
        this.alternatives = new ArrayList<>();
        this.alternatives.add(alternativeOne);
        this.stmt = stmt;
        this.paramIndex = paramIndex;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alternatives == null) ? 0 : alternatives.hashCode());
        result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
        result = prime * result + paramIndex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AlternativeReqPredicate other = (AlternativeReqPredicate) obj;
        if (alternatives == null) {
            if (other.alternatives != null) return false;
        } else if (!alternatives.equals(other.alternatives)) return false;
        if (stmt == null) {
            return other.stmt == null;
        } else if (!stmt.equals(other.stmt)) {
            return false;
        }

        return paramIndex == other.paramIndex;
    }

    public Statement getLocation() {
        return stmt;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    @Override
    public String toString() {
        return alternatives.stream()
                        .map(CrySLPredicate::toString)
                        .collect(Collectors.joining(" OR "))
                + " @ "
                + stmt
                + " @ index "
                + paramIndex;
    }

    @Override
    public String getName() {
        return alternatives.stream()
                .map(CrySLPredicate::getName)
                .collect(Collectors.joining(" OR "));
    }

    @Override
    public List<String> getInvolvedVarNames() {
        List<String> involvedVarNames = new ArrayList<>();
        for (CrySLPredicate alt : alternatives) {
            involvedVarNames.addAll(alt.getInvolvedVarNames());
        }
        return involvedVarNames;
    }

    public List<CrySLPredicate> getAlternatives() {
        return alternatives;
    }

    public void addAlternative(CrySLPredicate newAlt) {
        alternatives.add(newAlt);
    }
}
