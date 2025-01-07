package tests.error.nevertypeof;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class NeverTypeOfTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithoutConditionTest() {
        NeverTypeOf neverTypeOf = new NeverTypeOf();

        char[] value = new char[] {'a', 'l', 'l', 'o', 'w', 'e', 'd'};
        neverTypeOf.operation(value);

        Assertions.constraintErrors(neverTypeOf, 0);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        NeverTypeOf neverTypeOf = new NeverTypeOf();
        neverTypeOf.operation("notAllowed");

        Assertions.constraintErrors(neverTypeOf, 1);
    }

    @Test
    public void toCharArrayTest() {
        // Special case: toCharArray() is transformed from a string
        String string = "password";
        char[] transformedString = string.toCharArray();

        NeverTypeOf neverTypeOf = new NeverTypeOf();
        neverTypeOf.operation(transformedString);
        Assertions.extValue(0);

        Assertions.constraintErrors(neverTypeOf, 1);
    }
}
