package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.constraints.AlternativeReqPredicate;
import crypto.constraints.RequiredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLRule;
import java.util.ArrayList;
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
        StringBuilder builder = new StringBuilder();

        if (violatedPredicate.predicates().isEmpty()) {
            return "";
        }

        int index = violatedPredicate.predicates().stream().findFirst().get().index();
        builder.append(CrySLUtils.getIndexAsString(index));
        builder.append(" parameter was not properly generated as ");

        Collection<String> predNames = new ArrayList<>();
        for (RequiredPredicate predicate : violatedPredicate.predicates()) {
            predNames.add(predicate.predicate().getPredName());
        }

        String preds = String.join(" OR ", predNames);
        builder.append(preds);

        return builder.toString();
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
