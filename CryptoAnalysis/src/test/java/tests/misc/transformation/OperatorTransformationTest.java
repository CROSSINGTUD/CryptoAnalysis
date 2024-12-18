package tests.misc.transformation;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class OperatorTransformationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "transformation";
    }

    @Test
    public void positiveLengthExprTest() {
        byte[] correctBytes = new byte[] {'t', 'e', 's', 't'};

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeLengthExprTest() {
        byte[] incorrectBytes = new byte[] {'t', 'e', 's', 't', 'i', 'n', 'g'};

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(incorrectBytes.length);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }

    @Test
    public void positiveLengthFromStringTest() {
        byte[] correctBytes = "four".getBytes();

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeLengthFromStringTest() {
        byte[] correctBytes = "notFour".getBytes();

        OperatorConstraint constraint = new OperatorConstraint();
        constraint.lengthExprConstraint(correctBytes.length);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }
}
