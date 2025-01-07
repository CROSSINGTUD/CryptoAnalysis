package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class PredicateContradictionError extends AbstractConstraintsError {

    private final CrySLPredicate contradictedPredicate;
    private final int paramIndex;

    public PredicateContradictionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            CrySLPredicate contradictedPredicate,
            int paramIndex) {
        super(seed, errorStmt, rule);

        this.contradictedPredicate = contradictedPredicate;
        this.paramIndex = paramIndex;
    }

    public CrySLPredicate getContradictedPredicate() {
        return contradictedPredicate;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    @Override
    public String toErrorMarkerString() {
        return "Predicate " + contradictedPredicate + " is ensured although it should not";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate, paramIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof PredicateContradictionError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicate())
                && paramIndex == other.getParamIndex();
    }

    @Override
    public String toString() {
        return "PredicateContradictionError: " + toErrorMarkerString();
    }
}
