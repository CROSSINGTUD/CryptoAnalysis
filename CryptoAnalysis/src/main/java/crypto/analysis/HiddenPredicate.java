package crypto.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.CrySLPredicate;

import java.util.List;
import java.util.stream.Collectors;

public class HiddenPredicate extends EnsuredCrySLPredicate {

    private final AnalysisSeedWithSpecification generatingSeed;
    private final HiddenPredicateType type;

    public HiddenPredicate(CrySLPredicate predicate, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues2, AnalysisSeedWithSpecification generatingSeed, HiddenPredicateType type) {
        super(predicate, parametersToValues2);
        this.generatingSeed = generatingSeed;
        this.type = type;
    }

    public AnalysisSeedWithSpecification getGeneratingSeed() {
        return generatingSeed;
    }

    public enum HiddenPredicateType {
        GeneratingStateIsNeverReached,
        ConstraintsAreNotSatisfied,
        ConditionIsNotSatisfied
    }

    public HiddenPredicateType getType() {
        return type;
    }

    /**
     * Node: Errors are only in complete count at the end of the analysis.
     * @return errors list of all preceding errors
     */
    public List<AbstractError> getPrecedingErrors(){
        List<AbstractError> results = Lists.newArrayList();
        List<AbstractError> allErrors = generatingSeed.getErrors();
        switch(type) {
            case GeneratingStateIsNeverReached:
                List<AbstractError> typestateErrors = allErrors.stream().filter(e -> (e instanceof IncompleteOperationError || e instanceof TypestateError)).collect(Collectors.toList());
                if(typestateErrors.isEmpty()) {
                    // Seed object has no typestate errors that might be responsible for this hidden predicate
                    // TODO: report new info error type to report,
                    // 		that the seeds object could potentially ensure the missing predicate which might cause further subsequent errors
                    // 		but therefore requires a call to the predicate generating statement
                }

                // TODO: check whether the generating state is not reached due to a typestate error
                return allErrors;

            case ConstraintsAreNotSatisfied:
                // Generating state was reached but constraints are not satisfied.
                // Thus, return all constraints & required predicate errors.
                return allErrors.stream().filter(e -> (e instanceof RequiredPredicateError || e instanceof ConstraintError || e instanceof HardCodedError || e instanceof ImpreciseValueExtractionError || e instanceof InstanceOfError || e instanceof NeverTypeOfError)).collect(Collectors.toList());
            case ConditionIsNotSatisfied:
                // Generating state was reached but the predicates condition is not satisfied.
                // Thus, return all errors that causes the condition to be not satisfied
                List<AbstractError> precedingErrors = Lists.newArrayList(generatingSeed.retrieveErrorsForPredCondition(this.getPredicate()));
                // This method is called from a RequiredPredicateError that wants to retrieve its preceding errors.
                // In this case, preceding errors are not reported yet because the predicate condition wasn't required to be satisfied.
                // Since the hidden predicate is required to be an ensured predicate, we can assume the condition required to be satisfied.
                // Thus, we report all errors that causes the condition to be not satisfied.
                //precedingErrors.forEach(e -> this.generatingSeed.scanner.getAnalysisListener().reportError(generatingSeed, e));
                precedingErrors.forEach(e -> this.generatingSeed.scanner.getAnalysisReporter().reportError(generatingSeed, e));
                // Further, preceding errors can be of type RequiredPredicateError.
                // Thus, we have to recursively map preceding errors for the newly reported errors.
                for(AbstractError e: precedingErrors) {
                    if(e instanceof RequiredPredicateError) {
                        ((RequiredPredicateError)e).mapPrecedingErrors();
                    }
                }
                return precedingErrors;

        }
        return results;
    }
}
