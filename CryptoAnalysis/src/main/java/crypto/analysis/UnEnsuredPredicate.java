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

/**
 * Wrapper class for a single {@link CrySLPredicate} that could not be ensured during the analysis.
 * If a seed cannot generate a predicate due to some violations from its CrySL rule, the analysis
 * keeps track of those violations and the seed s.t. other seeds can reason about why the predicate
 * was not ensured. This way, the analysis can connect corresponding subsequent errors.
 *
 * <p>See the <a href="https://arxiv.org/abs/2403.07808">paper</a>
 */
public class UnEnsuredPredicate extends AbstractPredicate {

    private final AnalysisSeedWithSpecification generatingSeed;
    private final Collection<Violations> violations;

    /** Collection of violations that may cause a predicate to be not ensured */
    public enum Violations {
        /** Violation if there is a call to a method from the FORBIDDEN section. */
        CallToForbiddenMethod,

        /**
         * Violation if there is an unsatisfied constraint. Constraints include basic constraints
         * from the CONSTRAINTS section and required predicates from the REQUIRES section.
         */
        ConstraintsAreNotSatisfied,

        /**
         * Violation if the condition of predicate from the ENSURES section is not satisfied. Note
         * that the analysis does not report an error because the condition is not required to be
         * satisfied.
         */
        ConditionIsNotSatisfied,

        /**
         * Violation if there are dataflow paths where the seed does not reach an accepting state to
         * generate a predicate.
         */
        GeneratingStateMayNotBeReached,

        /**
         * Violation if there is no dataflow path where the seed reaches an accepting state to
         * generate a predicate.
         */
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

    /**
     * Compute the preceding errors that cause the predicate stored in this class to be not ensured.
     *
     * @return the errors that cause the predicate to be not ensured
     */
    public Collection<AbstractError> getPrecedingErrors() {
        Collection<AbstractError> result = new HashSet<>();

        // Collect all ForbiddenMethodErrors
        if (violations.contains(Violations.CallToForbiddenMethod)) {
            Collection<AbstractError> forbiddenMethodErrors =
                    generatingSeed.getErrors().stream()
                            .filter(e -> e instanceof ForbiddenMethodError)
                            .toList();
            result.addAll(forbiddenMethodErrors);
        }

        /* Collect the ConstraintErrors. This includes error violating constraints from the
         * CONSTRAINTS section and violated predicates from the REQUIRES section.
         */
        if (violations.contains(Violations.ConstraintsAreNotSatisfied)) {
            Collection<AbstractError> constraintErrors =
                    generatingSeed.getErrors().stream()
                            .filter(e -> e instanceof AbstractConstraintsError)
                            .toList();
            result.addAll(constraintErrors);
        }

        /* If the predicate condition in the ENSURES section is not satisfied, a
         * PredicateConstraintError is created. Note that these errors are not reported since
         * they are not required to be satisfied. However, a corresponding error indicate that
         * the condition is not satisfied.
         */
        if (violations.contains(Violations.ConditionIsNotSatisfied)) {
            PredicateConstraintError error =
                    new PredicateConstraintError(generatingSeed, getPredicate());
            result.add(error);
        }

        // Collect all errors that cause the seed to not reach an accepting state
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
