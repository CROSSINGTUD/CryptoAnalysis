package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import java.util.Arrays;

public class PredicateContradictionError extends AbstractError {

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
        return Arrays.hashCode(new Object[] {super.hashCode(), contradictedPredicate});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        PredicateContradictionError other = (PredicateContradictionError) obj;
        if (contradictedPredicate == null) {
            if (other.getContradictedPredicate() != null) return false;
        } else if (!contradictedPredicate.equals(other.getContradictedPredicate())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "PredicateContradictionError: " + toErrorMarkerString();
    }
}
