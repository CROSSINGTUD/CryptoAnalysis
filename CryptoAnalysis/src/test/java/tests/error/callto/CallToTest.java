package tests.error.callto;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class CallToTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithoutCondition() {
        // Required call to operation2
        CallTo callTo = new CallTo();
        callTo.operation2();

        Assertions.callToErrors(0);
    }

    @Test
    public void negativePredicateWithoutCondition() {
        // Missing required call to operation2
        CallTo callTo = new CallTo();
        callTo.operation1();

        Assertions.callToErrors(1);
    }

    @Test
    public void positivePredicateWithCondition() {
        // Condition is satisfied => Required call to operation3
        CallTo callTo1 = new CallTo(true);
        callTo1.operation2();
        callTo1.operation3();

        // Condition is not satisfied => Call to operation3 not required
        CallTo callTo2 = new CallTo(false);
        callTo2.operation2();

        Assertions.callToErrors(0);
    }

    @Test
    public void negativePredicateWithCondition() {
        // Condition is satisfied, but no call to operation3
        CallTo callTo = new CallTo(true);
        callTo.operation2();

        Assertions.constraintErrors(callTo, 1);
    }
}
