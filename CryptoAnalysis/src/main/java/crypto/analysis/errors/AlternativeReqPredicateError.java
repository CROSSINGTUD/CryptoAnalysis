package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.constraints.AlternativeReqPredicate;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

/**
 * Error that models a violation of required predicates with alternatives. Predicates from the
 * REQUIRES section may have the form
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...] || generatedPubKey[...] || generatedPrivKey[...];
 * }</pre>
 *
 * If the base predicate "generatedKey" and any (relevant) alternative is not ensured, an error of
 * this class is reported.
 */
public class AlternativeReqPredicateError extends AbstractRequiredPredicateError {

    private final AlternativeReqPredicate violatedPredicate;

    public AlternativeReqPredicateError(
            IAnalysisSeed seed,
            CrySLRule rule,
            AlternativeReqPredicate violatedPred,
            Collection<UnEnsuredPredicate> unEnsuredPredicates) {
        super(seed, violatedPred.statement(), rule, unEnsuredPredicates);

        this.violatedPredicate = violatedPred;
    }

    public AlternativeReqPredicate getViolatedPredicate() {
        return violatedPredicate;
    }

    @Override
    public String toErrorMarkerString() {
        return "";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), violatedPredicate);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof AlternativeReqPredicateError other
                && Objects.equals(violatedPredicate, other.getViolatedPredicate());
    }

    @Override
    public String toString() {
        return "AlternativeReqPredicateError: " + toErrorMarkerString();
    }
}
