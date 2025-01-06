package test;

import boomerang.scene.Val;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AlternativeReqPredicateError;
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
import java.util.Collection;
import test.assertions.CallToErrorCountAssertion;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.ConstraintViolationAssertion;
import test.assertions.DependentErrorAssertion;
import test.assertions.ForbiddenMethodErrorCountAssertion;
import test.assertions.ImpreciseValueExtractionErrorCountAssertion;
import test.assertions.IncompleteOperationErrorCountAssertion;
import test.assertions.InstanceOfErrorCountAssertion;
import test.assertions.MissingTypestateChange;
import test.assertions.NeverTypeOfErrorCountAssertion;
import test.assertions.NoCallToErrorCountAssertion;
import test.assertions.NoMissingTypestateChange;
import test.assertions.NotHardCodedErrorCountAssertion;
import test.assertions.PredicateContradictionErrorCountAssertion;
import test.assertions.PredicateErrorCountAssertion;
import test.assertions.TypestateErrorCountAssertion;

public class UsagePatternErrorListener implements IErrorListener {

    private final Collection<Assertion> assertions;

    public UsagePatternErrorListener(Collection<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public void reportError(CallToError callToError) {
        for (Assertion a : assertions) {
            if (a instanceof CallToErrorCountAssertion) {
                CallToErrorCountAssertion errorCountAssertion = (CallToErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(ConstraintError constraintError) {
        for (Assertion a : assertions) {
            if (a instanceof ConstraintErrorCountAssertion) {
                ConstraintErrorCountAssertion errorCountAssertion =
                        (ConstraintErrorCountAssertion) a;

                Collection<Val> values =
                        constraintError
                                .getSeed()
                                .getAnalysisResults()
                                .asStatementValWeightTable()
                                .columnKeySet();
                errorCountAssertion.increaseCount(values);
            }

            if (a instanceof ConstraintViolationAssertion) {
                ConstraintViolationAssertion violationAssertion = (ConstraintViolationAssertion) a;
                violationAssertion.reported(constraintError.getErrorStatement());
            }
        }
    }

    @Override
    public void reportError(ForbiddenMethodError forbiddenMethodError) {
        for (Assertion e : assertions) {
            if (e instanceof CallToForbiddenMethodAssertion) {
                CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) e;
                expectedResults.reported(forbiddenMethodError.getErrorStatement());
            }

            if (e instanceof ForbiddenMethodErrorCountAssertion) {
                ForbiddenMethodErrorCountAssertion assertion =
                        (ForbiddenMethodErrorCountAssertion) e;
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(HardCodedError hardCodedError) {
        for (Assertion a : assertions) {
            if (a instanceof NotHardCodedErrorCountAssertion) {
                NotHardCodedErrorCountAssertion assertion = (NotHardCodedErrorCountAssertion) a;
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(ImpreciseValueExtractionError impreciseValueExtractionError) {
        for (Assertion a : assertions) {
            if (a instanceof ImpreciseValueExtractionErrorCountAssertion) {
                ImpreciseValueExtractionErrorCountAssertion assertion =
                        (ImpreciseValueExtractionErrorCountAssertion) a;
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(IncompleteOperationError incompleteOperationError) {
        boolean hasTypestateChangeError = false;
        boolean expectsTypestateChangeError = false;
        for (Assertion a : assertions) {
            if (a instanceof MissingTypestateChange) {
                MissingTypestateChange missingTypestateChange = (MissingTypestateChange) a;
                if (missingTypestateChange
                        .getStmt()
                        .equals(incompleteOperationError.getErrorStatement())) {
                    missingTypestateChange.trigger();
                    hasTypestateChangeError = true;
                }
                expectsTypestateChangeError = true;
            }

            if (a instanceof NoMissingTypestateChange) {
                throw new RuntimeException("Reports a typestate error that should not be reported");
            }

            if (a instanceof IncompleteOperationErrorCountAssertion) {
                IncompleteOperationErrorCountAssertion errorCountAssertion =
                        (IncompleteOperationErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
        }
        if (hasTypestateChangeError != expectsTypestateChangeError) {
            throw new RuntimeException("Reports a typestate error that should not be reported");
        }
    }

    @Override
    public void reportError(InstanceOfError instanceOfError) {
        for (Assertion a : assertions) {
            if (a instanceof InstanceOfErrorCountAssertion) {
                InstanceOfErrorCountAssertion assertion = (InstanceOfErrorCountAssertion) a;
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(NeverTypeOfError neverTypeOfError) {
        for (Assertion a : assertions) {
            if (a instanceof NeverTypeOfErrorCountAssertion) {
                NeverTypeOfErrorCountAssertion assertion = (NeverTypeOfErrorCountAssertion) a;
                assertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(NoCallToError noCallToError) {
        for (Assertion a : assertions) {
            if (a instanceof CallToForbiddenMethodAssertion) {
                CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) a;
                expectedResults.reported(noCallToError.getErrorStatement());
            }

            if (a instanceof NoCallToErrorCountAssertion) {
                NoCallToErrorCountAssertion errorCountAssertion = (NoCallToErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
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
            if (a instanceof PredicateErrorCountAssertion) {
                PredicateErrorCountAssertion errorCountAssertion = (PredicateErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(AlternativeReqPredicateError alternativeReqPredicateError) {
        for (Assertion a : assertions) {
            if (a instanceof PredicateErrorCountAssertion) {
                PredicateErrorCountAssertion errorCountAssertion = (PredicateErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(TypestateError typestateError) {
        for (Assertion a : assertions) {
            if (a instanceof TypestateErrorCountAssertion) {
                TypestateErrorCountAssertion errorCountAssertion = (TypestateErrorCountAssertion) a;
                errorCountAssertion.increaseCount();
            }
        }
    }

    @Override
    public void reportError(UncaughtExceptionError uncaughtExceptionError) {}

    @Override
    public void reportError(AbstractError error) {
        for (Assertion a : assertions) {
            if (a instanceof DependentErrorAssertion) {
                DependentErrorAssertion depErrorAssertion = (DependentErrorAssertion) a;
                depErrorAssertion.addError(error);
            }
        }
    }
}
