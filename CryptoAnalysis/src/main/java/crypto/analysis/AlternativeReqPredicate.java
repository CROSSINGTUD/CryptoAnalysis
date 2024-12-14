package crypto.analysis;

import boomerang.scene.Statement;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        return Objects.hash(alternatives, stmt, paramIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AlternativeReqPredicate other
                && Objects.equals(alternatives, other.alternatives)
                && Objects.equals(stmt, other.stmt)
                && paramIndex == other.paramIndex;
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
