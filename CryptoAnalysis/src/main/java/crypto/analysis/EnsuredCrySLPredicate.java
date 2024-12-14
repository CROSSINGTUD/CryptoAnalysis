package crypto.analysis;

import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;

public class EnsuredCrySLPredicate {

    private final CrySLPredicate predicate;
    private final Collection<CallSiteWithExtractedValue> parametersToValues;

    public EnsuredCrySLPredicate(
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

    public String toString() {
        return "Proved " + predicate.getPredName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EnsuredCrySLPredicate other && predicate.equals(other.predicate);
    }
}
