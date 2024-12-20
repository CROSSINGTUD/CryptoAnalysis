package crypto.analysis;

import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;

/**
 * Super class for predicates that are propagated during the analysis. Each predicate is either an
 * {@link EnsuredPredicate} or an {@link UnEnsuredPredicate}. The former ones keep track of all
 * predicates from the ENSURES section where no violations from a rule are found. The latter ones
 * are propagated to keep track of predicates and seeds if there is a violation.
 */
public abstract class AbstractPredicate {

    private final CrySLPredicate predicate;
    private final Collection<CallSiteWithExtractedValue> parametersToValues;

    public AbstractPredicate(
            CrySLPredicate predicate, Collection<CallSiteWithExtractedValue> parametersToValues) {
        this.predicate = predicate;
        this.parametersToValues = parametersToValues;
    }

    public CrySLPredicate getPredicate() {
        return predicate;
    }

    public Collection<CallSiteWithExtractedValue> getParametersToValues() {
        return parametersToValues;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractPredicate other
                && Objects.equals(predicate, other.getPredicate());
    }
}
