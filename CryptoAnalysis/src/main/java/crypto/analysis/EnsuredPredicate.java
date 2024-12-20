package crypto.analysis;

import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;

/**
 * Wrapper class for a single {@link CrySLPredicate} to keep track of ensured predicate during the
 * analysis. A predicate is only ensured if there are no violations for a corresponding rule.
 * Otherwise, the analysis propagates an {@link UnEnsuredPredicate}.
 */
public class EnsuredPredicate extends AbstractPredicate {

    public EnsuredPredicate(
            CrySLPredicate predicate, Collection<CallSiteWithExtractedValue> parametersToValues) {
        super(predicate, parametersToValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof EnsuredPredicate;
    }

    @Override
    public String toString() {
        return "Ensured: " + getPredicate().getPredName();
    }
}
