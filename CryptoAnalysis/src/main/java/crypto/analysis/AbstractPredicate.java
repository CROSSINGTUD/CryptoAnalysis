package crypto.analysis;

import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.Objects;

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
