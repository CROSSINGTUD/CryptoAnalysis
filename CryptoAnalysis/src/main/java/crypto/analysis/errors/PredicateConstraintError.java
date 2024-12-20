package crypto.analysis.errors;

import crypto.analysis.AnalysisSeedWithSpecification;
import crysl.rule.CrySLPredicate;
import java.util.Objects;

/**
 * This class represents an internal error if the constraint of a predicate to be ensured is
 * violated and is only used to propagate HiddenPredicates. For example, we have the following
 * ENSURES block:
 *
 * <pre>{@code
 * ENSURES
 *    algorithm in {"AES"} => generatedKey[...]
 * }</pre>
 *
 * If the algorithm is not "AES", the predicate "generatedKey" is not ensured. Instead, the analysis
 * propagates a HiddenPredicate with the cause that the constraint is not satisfied to have a valid
 * reason. This class then simply indicates that the predicate's constraint is not satisfied. This
 * error is/should be not reported.
 */
public class PredicateConstraintError extends AbstractError {

    private final CrySLPredicate predicate;

    public PredicateConstraintError(AnalysisSeedWithSpecification seed, CrySLPredicate predicate) {
        super(seed, seed.getOrigin(), seed.getSpecification());

        this.predicate = predicate;
    }

    public CrySLPredicate getPredicate() {
        return predicate;
    }

    @Override
    public String toErrorMarkerString() {
        return "Cannot ensure predicate "
                + predicate.getPredName()
                + " because its constraint is not satisfied";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), predicate);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof PredicateConstraintError other
                && Objects.equals(predicate, other.getPredicate());
    }

    @Override
    public String toString() {
        return "PredicateConstraintError: " + toErrorMarkerString();
    }
}
