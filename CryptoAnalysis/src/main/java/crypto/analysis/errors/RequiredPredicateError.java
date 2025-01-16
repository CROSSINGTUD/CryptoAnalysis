package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

/**
 * This error models the violation of predicates from the REQUIRES section. An error is only
 * reported for single predicates, that is, predicates of the form
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...];
 * }</pre>
 *
 * If a predicate has alternatives, an {@link AlternativeReqPredicateError} is reported.
 */
public class RequiredPredicateError extends AbstractRequiredPredicateError {

    private final CrySLPredicate contradictedPredicate;
    private final int paramIndex;

    public RequiredPredicateError(
            IAnalysisSeed seed,
            Statement statement,
            CrySLRule rule,
            CrySLPredicate predicate,
            int paramIndex,
            Collection<UnEnsuredPredicate> unEnsuredPredicates) {
        super(seed, statement, rule, unEnsuredPredicates);

        this.contradictedPredicate = predicate;
        this.paramIndex = paramIndex;
    }

    public CrySLPredicate getContradictedPredicates() {
        return contradictedPredicate;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    @Override
    public String toErrorMarkerString() {
        StringBuilder msg = new StringBuilder(getParamIndexAsText(paramIndex));
        msg.append(" was not properly generated as ");
        String[] parts = contradictedPredicate.getPredName().split("(?=[A-Z])");
        msg.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            msg.append(parts[i]);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate, paramIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof RequiredPredicateError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicates())
                && paramIndex == other.getParamIndex();
    }

    @Override
    public String toString() {
        return "RequiredPredicateError: " + toErrorMarkerString();
    }
}
