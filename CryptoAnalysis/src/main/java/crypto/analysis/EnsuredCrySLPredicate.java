package crypto.analysis;

import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;

public class EnsuredCrySLPredicate extends AbstractPredicate {

    public EnsuredCrySLPredicate(
            CrySLPredicate predicate, Collection<CallSiteWithExtractedValue> parametersToValues) {
        super(predicate, parametersToValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof EnsuredCrySLPredicate;
    }

    @Override
    public String toString() {
        return "Ensured: " + getPredicate().getPredName();
    }
}
