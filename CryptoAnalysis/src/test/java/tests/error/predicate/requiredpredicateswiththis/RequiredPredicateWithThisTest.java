package tests.error.predicate.requiredpredicateswiththis;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class RequiredPredicateWithThisTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "requiredPredicateWithThis";
    }

    @Test
    public void positiveRequiredPredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(false);

        SimpleTarget target = source.generateTarget();
        Assertions.hasEnsuredPredicate(target, "generatedTarget");
        target.doNothing();

        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.hasGeneratedPredicate(usingTarget);
        Assertions.hasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(0);
    }

    @Test
    public void negativeRequiredPredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(true);

        // No ensured predicate is passed to target -> RequiredPredicateError
        SimpleTarget target = source.generateTarget();
        target.doNothing();
        Assertions.notHasEnsuredPredicate(target);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.notHasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(2);
    }

    @Test
    public void positiveRequiredAlternativePredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(false);

        TargetWithAlternatives target1 = source.generateTargetWithAlternatives();
        Assertions.hasEnsuredPredicate(target1, "generatedTargetWithAlternatives");
        target1.doNothing();

        UsingTarget usingTarget1 = new UsingTarget();
        usingTarget1.useTarget(target1);
        Assertions.hasEnsuredPredicate(usingTarget1);

        TargetWithAlternatives target2 = source.generateTargetAlternative1();
        Assertions.hasEnsuredPredicate(target2, "generatedAlternative1");
        target2.doNothing();

        UsingTarget usingTarget2 = new UsingTarget();
        usingTarget2.useTarget(target2);
        Assertions.hasEnsuredPredicate(usingTarget2);

        TargetWithAlternatives target3 = source.generateTargetAlternative2();
        Assertions.hasEnsuredPredicate(target3, "generatedAlternative2");
        target3.doNothing();

        UsingTarget usingTarget3 = new UsingTarget();
        usingTarget3.useTarget(target3);
        Assertions.hasEnsuredPredicate(usingTarget3);

        Assertions.predicateErrors(0);
    }

    @Test
    public void negativeRequiredAlternativePredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(true);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target1 = source.generateTargetWithAlternatives();
        target1.doNothing();
        Assertions.notHasEnsuredPredicate(target1);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget1 = new UsingTarget();
        usingTarget1.useTarget(target1);
        Assertions.notHasEnsuredPredicate(usingTarget1);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target2 = source.generateTargetAlternative1();
        target2.doNothing();
        Assertions.notHasEnsuredPredicate(target2);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget2 = new UsingTarget();
        usingTarget2.useTarget(target2);
        Assertions.notHasEnsuredPredicate(usingTarget2);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target3 = source.generateTargetAlternative2();
        target3.doNothing();
        Assertions.notHasEnsuredPredicate(target3);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget3 = new UsingTarget();
        usingTarget3.useTarget(target3);
        Assertions.notHasEnsuredPredicate(usingTarget3);

        Assertions.predicateErrors(6);
    }
}
