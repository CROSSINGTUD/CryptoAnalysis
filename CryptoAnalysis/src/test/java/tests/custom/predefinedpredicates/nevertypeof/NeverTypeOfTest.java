package tests.custom.predefinedpredicates.nevertypeof;

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

        char[] value = new char[]{'a', 'l', 'l', 'o', 'w', 'e', 'd'};
        neverTypeOf.operation(value);

        Assertions.neverTypeOfErrors(0);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        NeverTypeOf neverTypeOf = new NeverTypeOf();
        neverTypeOf.operation("notAllowed");

        Assertions.neverTypeOfErrors(1);
    }
}
