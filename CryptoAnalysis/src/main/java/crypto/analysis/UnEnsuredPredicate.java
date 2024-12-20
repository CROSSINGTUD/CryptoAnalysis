package crypto.analysis;

import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AbstractOrderError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.PredicateConstraintError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UnEnsuredPredicate extends AbstractPredicate {

    private final AnalysisSeedWithSpecification generatingSeed;
    private final Collection<Violations> violations;

    public enum Violations {
        CallToForbiddenMethod,
        ConstraintsAreNotSatisfied,
        ConditionIsNotSatisfied,
        GeneratingStateMayNotBeReached,
        GeneratingStateIsNeverReached
    }

    public UnEnsuredPredicate(
            CrySLPredicate predicate,
            Collection<CallSiteWithExtractedValue> parametersToValues,
            AnalysisSeedWithSpecification generatingSeed,
            Collection<Violations> violations) {
        super(predicate, parametersToValues);

        this.generatingSeed = generatingSeed;
        this.violations = Set.copyOf(violations);
    }

    public AnalysisSeedWithSpecification getGeneratingSeed() {
        return generatingSeed;
    }

    public Collection<Violations> getViolations() {
        return violations;
    }

    public Collection<AbstractError> getPrecedingErrors() {
        Collection<AbstractError> result = new HashSet<>();

        if (violations.contains(Violations.CallToForbiddenMethod)) {
            Collection<AbstractError> forbiddenMethodErrors =
                    generatingSeed.getErrors().stream()
                            .filter(e -> e instanceof ForbiddenMethodError)
                            .toList();
            result.addAll(forbiddenMethodErrors);
        }

        if (violations.contains(Violations.ConstraintsAreNotSatisfied)) {
            Collection<AbstractError> constraintErrors =
                    generatingSeed.getErrors().stream()
                            .filter(e -> e instanceof AbstractConstraintsError)
                            .toList();
            result.addAll(constraintErrors);
        }

        if (violations.contains(Violations.ConditionIsNotSatisfied)) {
            PredicateConstraintError error =
                    new PredicateConstraintError(generatingSeed, getPredicate());
            result.add(error);
        }

        if (violations.contains(Violations.GeneratingStateMayNotBeReached)
                || violations.contains(Violations.GeneratingStateIsNeverReached)) {
            Collection<AbstractError> orderError =
                    generatingSeed.getErrors().stream()
                            .filter(e -> e instanceof AbstractOrderError)
                            .toList();
            result.addAll(orderError);
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), generatingSeed, violations);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof UnEnsuredPredicate other
                && Objects.equals(generatingSeed, other.getGeneratingSeed())
                && Objects.equals(violations, other.getViolations());
    }

    @Override
    public String toString() {
        return "Hidden: " + getPredicate().getPredName();
    }
}
