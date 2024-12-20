package crypto.analysis.errors;

import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.RequiredCrySLPredicate;
import crypto.analysis.UnEnsuredPredicate;
import crysl.rule.CrySLPredicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    private final Collection<CrySLPredicate> contradictedPredicate;
    private final Collection<RequiredCrySLPredicate> relevantPredicates;

    public AlternativeReqPredicateError(
            AnalysisSeedWithSpecification seed,
            AlternativeReqPredicate violatedPred,
            Collection<UnEnsuredPredicate> unEnsuredPredicates) {
        super(seed, violatedPred.getLocation(), seed.getSpecification(), unEnsuredPredicates);

        this.contradictedPredicate = List.copyOf(violatedPred.getAllAlternatives());
        this.relevantPredicates = Set.copyOf(violatedPred.getRelAlternatives());
    }

    public Collection<CrySLPredicate> getContradictedPredicate() {
        return contradictedPredicate;
    }

    public Collection<RequiredCrySLPredicate> getRelevantPredicates() {
        return relevantPredicates;
    }

    @Override
    public String toErrorMarkerString() {
        Collection<CrySLPredicate> added = new HashSet<>();

        List<String> msg = new ArrayList<>();
        for (RequiredCrySLPredicate pred : relevantPredicates) {
            StringBuilder temp = new StringBuilder();
            temp.append(getParamIndexAsText(pred.getParamIndex()));

            if (pred.getPred().isNegated()) {
                temp.append(" is generated as ");
            } else {
                temp.append(" was not properly generated as ");
            }
            temp.append(pred.getPred().getPredName());

            msg.add(temp.toString());
            added.add(pred.getPred());
        }

        for (CrySLPredicate pred : contradictedPredicate) {
            if (added.contains(pred)) {
                continue;
            }
            msg.add(pred.getPredName() + " was not ensured (not relevant)");
        }

        return String.join(" AND ", msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate, relevantPredicates);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof AlternativeReqPredicateError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicate())
                && Objects.equals(relevantPredicates, other.getRelevantPredicates());
    }

    @Override
    public String toString() {
        return "AlternativeReqPredicateError: " + toErrorMarkerString();
    }
}
