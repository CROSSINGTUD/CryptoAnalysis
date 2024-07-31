package tests.misc.transformation;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import java.util.UUID;

public class StringTransformationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "transformation";
    }

    @Test
    public void positiveToCharArrayTest() {
        // Test toCharArray() with a random String -> not hard coded
        char[] password = UUID.randomUUID().toString().toCharArray();

        StringConstraint constraint = new StringConstraint();
        constraint.toCharArrayConstraint(password);

        Assertions.notHardCodedErrors(0);
    }

    @Test
    public void negativeToCharArrayTest() {
        // Test toCharArray() with a fixed String -> hard coded
        char[] password = "password".toCharArray();

        StringConstraint constraint = new StringConstraint();
        constraint.toCharArrayConstraint(password);
        Assertions.extValue(0);

        Assertions.notHardCodedErrors(1);
    }

    @Test
    public void positiveReplaceCharSequenceTest() {
        // Test replace(CharSequence, CharSequence) with replacing incorrect to correct String
        String string = "DES".replace("D", "A");

        StringConstraint constraint1 = new StringConstraint();
        constraint1.replaceConstraint(string);
        Assertions.extValue(0);

        Assertions.constraintErrors(0);
    }

    @Test
    public void negativeReplaceCharSequenceTest() {
        // Test replace(CharSequence, CharSequence) with replacing correct with incorrect String
        String string = "AES".replace("A", "D");

        StringConstraint constraint = new StringConstraint();
        constraint.replaceConstraint(string);
        Assertions.extValue(0);
        Assertions.violatedConstraint();

        Assertions.constraintErrors(1);
    }
}
