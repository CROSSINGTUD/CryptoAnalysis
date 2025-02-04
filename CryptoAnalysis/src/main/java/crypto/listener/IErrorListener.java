package crypto.listener;

import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;

public interface IErrorListener {

    void reportError(ConstraintError constraintError);

    void reportError(ForbiddenMethodError forbiddenMethodError);

    void reportError(ImpreciseValueExtractionError impreciseValueExtractionError);

    void reportError(IncompleteOperationError incompleteOperationError);

    void reportError(PredicateContradictionError predicateContradictionError);

    void reportError(RequiredPredicateError requiredPredicateError);

    void reportError(AlternativeReqPredicateError alternativeReqPredicateError);

    void reportError(TypestateError typestateError);

    void reportError(UncaughtExceptionError uncaughtExceptionError);

    void reportError(AbstractError error);
}
