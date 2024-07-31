package tests.misc.transformation;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class WrapperTransformationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "transformation";
    }

    @Test
    public void positiveIntegerParseIntTest() {
        String correctValue = "10";
        int intValue = Integer.parseInt(correctValue);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(intValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeIntegerParseIntTest() {
        String incorrectValue = "9999";
        int intValue = Integer.parseInt(incorrectValue);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(intValue);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }
}
