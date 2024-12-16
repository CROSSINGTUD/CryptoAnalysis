package crypto.analysis.errors;

import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.HiddenPredicate;
import crypto.analysis.RequiredCrySLPredicate;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates {@link RequiredPredicateError} for all Required Predicate error generates
 * RequiredPredicateError
 */
public class RequiredPredicateError extends AbstractConstraintsError {

    private final Collection<CrySLPredicate> contradictedPredicates;
    private final Collection<HiddenPredicate> hiddenPredicates;
    private final int paramIndex;

    public RequiredPredicateError(
            AnalysisSeedWithSpecification seed,
            RequiredCrySLPredicate violatedPred,
            Collection<HiddenPredicate> hiddenPredicates) {
        super(seed, violatedPred.getLocation(), seed.getSpecification());

        this.hiddenPredicates = Set.copyOf(hiddenPredicates);
        this.contradictedPredicates = List.of(violatedPred.getPred());
        this.paramIndex = violatedPred.getParamIndex();
    }

    public RequiredPredicateError(
            AnalysisSeedWithSpecification seed,
            AlternativeReqPredicate violatedPred,
            Collection<HiddenPredicate> hiddenPredicates) {
        super(seed, violatedPred.getLocation(), seed.getSpecification());

        this.hiddenPredicates = Set.copyOf(hiddenPredicates);
        this.contradictedPredicates = List.copyOf(violatedPred.getAlternatives());
        this.paramIndex = violatedPred.getParamIndex();
    }

    public void mapPrecedingErrors() {
        for (HiddenPredicate hiddenPredicate : hiddenPredicates) {
            Collection<AbstractError> precedingErrors = hiddenPredicate.getPrecedingErrors();
            this.addCausingError(precedingErrors);
            precedingErrors.forEach(e -> e.addSubsequentError(this));
        }
    }

    public Collection<CrySLPredicate> getContradictedPredicates() {
        return contradictedPredicates;
    }

    public Collection<HiddenPredicate> getHiddenPredicates() {
        return hiddenPredicates;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    @Override
    public String toErrorMarkerString() {
        StringBuilder msg = new StringBuilder(getParamIndexAsText());
        msg.append(" was not properly generated as ");
        String predicateName =
                getContradictedPredicates().stream()
                        .map(CrySLPredicate::getPredName)
                        .collect(Collectors.joining(" OR "));
        String[] parts = predicateName.split("(?=[A-Z])");
        msg.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            msg.append(parts[i]);
        }
        return msg.toString();
    }

    private String getParamIndexAsText() {
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
        return Objects.hash(super.hashCode(), hiddenPredicates, contradictedPredicates, paramIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof RequiredPredicateError other
                && Objects.equals(contradictedPredicates, other.getContradictedPredicates())
                && Objects.equals(hiddenPredicates, other.getHiddenPredicates())
                && paramIndex == other.getParamIndex();
    }

    @Override
    public String toString() {
        return "RequiredPredicateError: " + toErrorMarkerString();
    }
}
