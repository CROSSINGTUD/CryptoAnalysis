package crypto.analysis;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import crypto.listener.IErrorListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ErrorCollector implements IErrorListener {

    private final Table<WrappedClass, Method, Set<AbstractError>> errorCollection = HashBasedTable.create();

    public Table<WrappedClass, Method, Set<AbstractError>> getErrorCollection() {
        return errorCollection;
    }

    private void addErrorToCollection(AbstractError error) {
        Method method = error.getErrorStatement().getMethod();
        WrappedClass wrappedClass = method.getDeclaringClass();

        Set<AbstractError> errorsForClass = errorCollection.get(wrappedClass, method);
        if (errorsForClass == null) {
            errorsForClass = new HashSet<>();
        }

        errorsForClass.add(error);
        errorCollection.put(wrappedClass, method, errorsForClass);
    }

    @Override
    public void reportError(CallToError callToError) {
        addErrorToCollection(callToError);
    }

    @Override
    public void reportError(ConstraintError constraintError) {
        addErrorToCollection(constraintError);
    }

    @Override
    public void reportError(ForbiddenMethodError forbiddenMethodError) {
        addErrorToCollection(forbiddenMethodError);
    }

    @Override
    public void reportError(HardCodedError hardCodedError) {
        addErrorToCollection(hardCodedError);
    }

    @Override
    public void reportError(ImpreciseValueExtractionError impreciseValueExtractionError) {
        addErrorToCollection(impreciseValueExtractionError);
    }

    @Override
    public void reportError(IncompleteOperationError incompleteOperationError) {
        addErrorToCollection(incompleteOperationError);
    }

    @Override
    public void reportError(InstanceOfError instanceOfError) {
        addErrorToCollection(instanceOfError);
    }

    @Override
    public void reportError(NeverTypeOfError neverTypeOfError) {
        addErrorToCollection(neverTypeOfError);
    }

    @Override
    public void reportError(NoCallToError noCallToError) {
        addErrorToCollection(noCallToError);
    }

    @Override
    public void reportError(PredicateContradictionError predicateContradictionError) {
        addErrorToCollection(predicateContradictionError);
    }

    @Override
    public void reportError(RequiredPredicateError requiredPredicateError) {
        addErrorToCollection(requiredPredicateError);
    }

    @Override
    public void reportError(TypestateError typestateError) {
        addErrorToCollection(typestateError);
    }

    @Override
    public void reportError(UncaughtExceptionError uncaughtExceptionError) {
        addErrorToCollection(uncaughtExceptionError);
    }

    @Override
    public void reportError(AbstractError error) {
        addErrorToCollection(error);
    }
}
