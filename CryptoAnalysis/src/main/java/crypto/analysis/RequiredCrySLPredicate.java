package crypto.analysis;

import boomerang.scene.Statement;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.List;

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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((statement == null) ? 0 : statement.hashCode());
        result = prime * result + paramIndex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RequiredCrySLPredicate other = (RequiredCrySLPredicate) obj;
        if (predicate == null) {
            if (other.predicate != null) return false;
        } else if (!predicate.equals(other.predicate)) return false;
        if (statement == null) {
            if (other.statement != null) {
                return false;
            }
        } else if (!statement.equals(other.statement)) {
            return false;
        }
        return paramIndex == other.paramIndex;
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
