package tests.misc.transformation;

import java.math.BigInteger;
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
        int correctValue = Integer.parseInt("10");

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(correctValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeIntegerParseIntTest() {
        int incorrectValue = Integer.parseInt("9999");

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.integerParseIntConstraint(incorrectValue);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }

    @Test
    public void positiveBigIntegerValueOfTest() {
        BigInteger correctValue = BigInteger.valueOf(100000);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.bigIntegerValueOfConstraint(correctValue);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeBigIntegerValueOfTest() {
        BigInteger incorrectValue = BigInteger.valueOf(10);

        WrapperConstraint constraint = new WrapperConstraint();
        constraint.bigIntegerValueOfConstraint(incorrectValue);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }
}
