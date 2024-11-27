package crypto.analysis.errors;

import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.HiddenPredicate;
import crypto.analysis.RequiredCrySLPredicate;
import crysl.rule.CrySLPredicate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Creates {@link RequiredPredicateError} for all Required Predicate error generates
 * RequiredPredicateError
 */
public class RequiredPredicateError extends AbstractError {

    private final Collection<HiddenPredicate> hiddenPredicates;
    private final Collection<CrySLPredicate> contradictedPredicates;
    private final int paramIndex;

    public RequiredPredicateError(
            AnalysisSeedWithSpecification seed, RequiredCrySLPredicate violatedPred) {
        super(seed, violatedPred.getLocation(), seed.getSpecification());

        this.hiddenPredicates = new HashSet<>();
        this.contradictedPredicates = Collections.singletonList(violatedPred.getPred());
        this.paramIndex = violatedPred.getParamIndex();
    }

    public RequiredPredicateError(
            AnalysisSeedWithSpecification seed, AlternativeReqPredicate violatedPred) {
        super(seed, violatedPred.getLocation(), seed.getSpecification());

        this.hiddenPredicates = new HashSet<>();
        this.contradictedPredicates = violatedPred.getAlternatives();
        this.paramIndex = violatedPred.getParamIndex();
    }

    public void addHiddenPredicates(Collection<HiddenPredicate> hiddenPredicates) {
        this.hiddenPredicates.addAll(hiddenPredicates);
    }

    public void mapPrecedingErrors() {
        for (HiddenPredicate hiddenPredicate : hiddenPredicates) {
            Collection<AbstractError> precedingErrors = hiddenPredicate.getPrecedingErrors();
            this.addCausingError(precedingErrors);
            precedingErrors.forEach(e -> e.addSubsequentError(this));
        }
    }

    /**
     * This method returns a list of contradicting predicates
     *
     * @return list of contradicting predicates
     */
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
        String res;
        switch (paramIndex) {
            case -1:
                return "Return value";
            case 0:
                res = "First ";
                break;
            case 1:
                res = "Second ";
                break;
            case 2:
                res = "Third ";
                break;
            case 3:
                res = "Fourth ";
                break;
            case 4:
                res = "Fifth ";
                break;
            case 5:
                res = "Sixth ";
                break;
            default:
                res = (paramIndex + 1) + "th ";
                break;
        }
        res += "parameter";
        return res;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(
                new Object[] {
                    super.hashCode(), hiddenPredicates, contradictedPredicates, paramIndex
                });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        RequiredPredicateError other = (RequiredPredicateError) obj;
        if (hiddenPredicates == null) {
            if (other.getHiddenPredicates() != null) return false;
        } else if (!hiddenPredicates.equals(other.hiddenPredicates)) {
            return false;
        }

        if (contradictedPredicates == null) {
            if (other.getContradictedPredicates() != null) return false;
        } else if (!contradictedPredicates.equals(other.getContradictedPredicates())) {
            return false;
        }

        return paramIndex == other.getParamIndex();
    }

    @Override
    public String toString() {
        return "RequiredPredicateError: " + toErrorMarkerString();
    }
}
