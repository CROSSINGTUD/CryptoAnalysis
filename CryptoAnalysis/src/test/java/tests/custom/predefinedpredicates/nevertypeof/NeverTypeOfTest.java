package tests.custom.predefinedpredicates.nevertypeof;

import crypto.analysis.CrySLRulesetSelector;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class NeverTypeOfTest extends UsagePatternTestingFramework {

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
