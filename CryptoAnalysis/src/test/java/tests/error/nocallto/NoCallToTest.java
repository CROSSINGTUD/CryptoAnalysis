package tests.error.nocallto;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class NoCallToTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithoutConditionTest() {
        NoCallTo noCallTo = new NoCallTo();

        // operation2 should not be called in any case
        noCallTo.operation2();
        Assertions.callToForbiddenMethod();

        Assertions.noCallToErrors(1);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        NoCallTo noCallTo = new NoCallTo();

        // operation2 is never called
        noCallTo.operation1();

        Assertions.noCallToErrors(0);
    }

    @Test
    public void positivePredicateWithConditionTest() {
        NoCallTo noCallTo = new NoCallTo(true);

        // Condition is satisfied => Call to operation3 not allowed
        noCallTo.operation3();
        Assertions.callToForbiddenMethod();

        Assertions.noCallToErrors(1);
    }

    @Test
    public void negativePredicateWithConditionTest() {
        NoCallTo noCallTo = new NoCallTo(false);

        // Condition is not satisfied => Call to operation3 is allowed
        noCallTo.operation3();

        Assertions.noCallToErrors(0);
    }
}
