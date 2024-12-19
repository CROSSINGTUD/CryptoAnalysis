package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.HiddenPredicate;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Super class for errors that work with a {@link HiddenPredicate}. Currently, there are {@link
 * RequiredPredicateError} that hold errors with single violated predicates and {@link
 * AlternativeReqPredicateError} that hold errors for violated predicates with alternatives.
 */
public abstract class AbstractRequiredPredicateError extends AbstractConstraintsError {

    private final Collection<HiddenPredicate> hiddenPredicates;

    public AbstractRequiredPredicateError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<HiddenPredicate> hiddenPredicates) {
        super(seed, errorStmt, rule);

        this.hiddenPredicates = Set.copyOf(hiddenPredicates);
    }

    public Collection<HiddenPredicate> getHiddenPredicates() {
        return hiddenPredicates;
    }

    protected String getParamIndexAsText(int paramIndex) {
        return switch (paramIndex) {
            case -1 -> "Return value";
            case 0 -> "First parameter";
            case 1 -> "Second parameter";
            case 2 -> "Third parameter";
            case 3 -> "Fourth parameter";
            case 4 -> "Fifth parameter";
            case 5 -> "Sixth parameter";
            default -> (paramIndex + 1) + "th parameter";
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hiddenPredicates);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof AbstractRequiredPredicateError other
                && Objects.equals(hiddenPredicates, other.getHiddenPredicates());
    }
}
