package tests.custom.predefinedpredicates.instance;

import crypto.analysis.CrySLRulesetSelector;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class InstanceOfTest extends UsagePatternTestingFramework {

    @Override
    protected CrySLRulesetSelector.Ruleset getRuleSet() {
        return CrySLRulesetSelector.Ruleset.CustomRules;
    }

    @Override
    protected String getRulesetPath() {
        return "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithoutConditionTest() {
        InstanceOf instanceOf = new InstanceOf();

        // Any super class is allowed
        instanceOf.operation1(new SuperClass());
        instanceOf.operation1(new SubClass());

        Assertions.instanceOfErrors(0);
    }

    @Test
    public void negativePredicateWithoutConditionTest() {
        InstanceOf instanceOf = new InstanceOf();

        // Call requires explicit instance of sub class
        SuperClass classismus = new SuperClass();
        instanceOf.operation2(classismus);

        Assertions.instanceOfErrors(1);
    }
}
