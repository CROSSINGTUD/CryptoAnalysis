package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class PredicateContradictionError extends AbstractRequiresError {

    private final CrySLPredicate contradictedPredicate;

    public PredicateContradictionError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            CrySLPredicate contradictedPredicate) {
        super(seed, errorStmt, rule);

        this.contradictedPredicate = contradictedPredicate;
    }

    public CrySLPredicate getContradictedPredicate() {
        return contradictedPredicate;
    }

    @Override
    public String toErrorMarkerString() {
        return "Predicate " + contradictedPredicate + " is ensured although it should not";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof PredicateContradictionError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicate());
    }

    @Override
    public String toString() {
        return "PredicateContradictionError: " + toErrorMarkerString();
    }
}
