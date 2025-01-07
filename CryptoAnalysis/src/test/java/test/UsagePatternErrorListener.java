package test;

import boomerang.scene.Val;
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
import crypto.listener.IErrorListener;
import java.util.Collection;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.ConstraintViolationAssertion;
import test.assertions.DependentErrorAssertion;
import test.assertions.ForbiddenMethodErrorCountAssertion;
import test.assertions.ImpreciseValueExtractionErrorCountAssertion;
import test.assertions.IncompleteOperationErrorCountAssertion;
import test.assertions.MissingTypestateChange;
import test.assertions.NoMissingTypestateChange;
import test.assertions.PredicateContradictionErrorCountAssertion;
import test.assertions.PredicateErrorCountAssertion;
import test.assertions.TypestateErrorCountAssertion;

public class UsagePatternErrorListener implements IErrorListener {

    private final Collection<Assertion> assertions;

    public UsagePatternErrorListener(Collection<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public void reportError(ConstraintError constraintError) {
        for (Assertion a : assertions) {
            if (a instanceof ConstraintErrorCountAssertion assertion) {
                Collection<Val> values =
                        constraintError
                                .getSeed()
                                .getAnalysisResults()
                                .asStatementValWeightTable()
                                .columnKeySet();
                assertion.increaseCount(values);
            }

            if (a instanceof ConstraintViolationAssertion assertion) {
                assertion.reported(constraintError.getErrorStatement());
            }
        }
    }

    @Override
    public void reportError(ForbiddenMethodError forbiddenMethodError) {
        for (Assertion e : assertions) {
            if (e instanceof CallToForbiddenMethodAssertion assertion) {
                assertion.reported(forbiddenMethodError.getErrorStatement());
            }

            if (e instanceof ForbiddenMethodErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(ImpreciseValueExtractionError impreciseValueExtractionError) {
        for (Assertion a : assertions) {
            if (a instanceof ImpreciseValueExtractionErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(IncompleteOperationError incompleteOperationError) {
        boolean hasTypestateChangeError = false;
        boolean expectsTypestateChangeError = false;
        for (Assertion a : assertions) {
            if (a instanceof MissingTypestateChange assertion) {
                if (assertion.getStmt().equals(incompleteOperationError.getErrorStatement())) {
                    assertion.trigger();
                    hasTypestateChangeError = true;
                }
                expectsTypestateChangeError = true;
            }

            if (a instanceof NoMissingTypestateChange) {
                throw new RuntimeException("Reports a typestate error that should not be reported");
            }

            if (a instanceof IncompleteOperationErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
        if (hasTypestateChangeError != expectsTypestateChangeError) {
            throw new RuntimeException("Reports a typestate error that should not be reported");
        }
    }

    @Override
    public void reportError(PredicateContradictionError predicateContradictionError) {
        for (Assertion a : assertions) {
            if (a instanceof PredicateContradictionErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(RequiredPredicateError requiredPredicateError) {
        for (Assertion a : assertions) {
            if (a instanceof PredicateErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(AlternativeReqPredicateError alternativeReqPredicateError) {
        for (Assertion a : assertions) {
            if (a instanceof PredicateErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(TypestateError typestateError) {
        for (Assertion a : assertions) {
            if (a instanceof TypestateErrorCountAssertion assertion) {
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(UncaughtExceptionError uncaughtExceptionError) {}

    @Override
    public void reportError(AbstractError error) {
        for (Assertion a : assertions) {
            if (a instanceof DependentErrorAssertion assertion) {
                assertion.addError(error);
            }
        }
    }
}
