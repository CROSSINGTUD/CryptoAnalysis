package tests.error.instance;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class InstanceOfTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithoutConditionTest() {
        InstanceOf instanceOf = new InstanceOf();

        // Any super class is allowed
        instanceOf.operation1(new SuperClass());
        instanceOf.operation1(new SubClass());

        Assertions.constraintErrors(instanceOf, 0);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        InstanceOf instanceOf = new InstanceOf();

        // Call requires explicit instance of sub class
        SuperClass superClass = new SuperClass();
        instanceOf.operation2(superClass);

        Assertions.constraintErrors(instanceOf, 1);
    }
}
