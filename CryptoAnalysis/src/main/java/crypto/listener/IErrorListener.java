package crypto.listener;

import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.NoCallToError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;

public interface IErrorListener {

    void reportError(CallToError callToError);

    void reportError(ConstraintError constraintError);

    void reportError(ForbiddenMethodError forbiddenMethodError);

    void reportError(HardCodedError hardCodedError);

    void reportError(ImpreciseValueExtractionError impreciseValueExtractionError);

    void reportError(IncompleteOperationError incompleteOperationError);

    void reportError(InstanceOfError instanceOfError);

    void reportError(NeverTypeOfError neverTypeOfError);

    void reportError(NoCallToError noCallToError);

    void reportError(PredicateContradictionError predicateContradictionError);

    void reportError(RequiredPredicateError requiredPredicateError);

    void reportError(TypestateError typestateError);

    void reportError(UncaughtExceptionError uncaughtExceptionError);

    void reportError(AbstractError error);
}
