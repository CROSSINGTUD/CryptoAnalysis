package tests.custom.predicate;

import crypto.analysis.CrySLRulesetSelector;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class RequiredPredicateWithThisTest extends UsagePatternTestingFramework {

    @Override
    protected CrySLRulesetSelector.Ruleset getRuleSet() {
        return CrySLRulesetSelector.Ruleset.CustomRules;
    }

    @Override
    protected String getRulesetPath() {
        return "requiredPredicateWithThis";
    }

    @Test
    public void positiveRequiredPredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(false);

        SimpleTarget target = source.generateTarget();
        target.doNothing();
        Assertions.hasEnsuredPredicate(target, "generatedTarget");

        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
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

        TargetWithAlternatives target = source.generateTargetWithAlternatives();
        target.doNothing("Nothing");
        Assertions.hasEnsuredPredicate(target, "generatedTargetWithAlternatives");

        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.hasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(0);
    }

    @Test
    public void negativeRequiredAlternativePredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(true);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target = source.generateTargetWithAlternatives();
        target.doNothing("Nothing");
        Assertions.notHasEnsuredPredicate(target);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.notHasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(2);
    }
}
