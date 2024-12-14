package crypto.analysis;

import boomerang.scene.Statement;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.List;
import java.util.Objects;

public class RequiredCrySLPredicate implements ISLConstraint {

    private final CrySLPredicate predicate;
    private final Statement statement;
    private final int paramIndex;

    public RequiredCrySLPredicate(CrySLPredicate predicate, Statement statement, int paramIndex) {
        this.predicate = predicate;
        this.statement = statement;
        this.paramIndex = paramIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, statement, paramIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RequiredCrySLPredicate other
                && Objects.equals(predicate, other.predicate)
                && Objects.equals(statement, other.statement)
                && paramIndex == other.paramIndex;
    }

    public CrySLPredicate getPred() {
        return predicate;
    }

    public Statement getLocation() {
        return statement;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    @Override
    public String toString() {
        return predicate + " @ " + statement.toString() + " @ index " + paramIndex;
    }

    @Override
    public String getName() {
        return predicate.getName();
    }

    @Override
    public List<String> getInvolvedVarNames() {
        return predicate.getInvolvedVarNames();
    }
}
