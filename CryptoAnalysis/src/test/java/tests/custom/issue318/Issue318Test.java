package tests.custom.issue318;

import crypto.analysis.CrySLRulesetSelector;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class Issue318Test extends UsagePatternTestingFramework {

    @Override
    protected CrySLRulesetSelector.Ruleset getRuleSet() {
        return CrySLRulesetSelector.Ruleset.CustomRules;
    }

    @Override
    protected String getRulesetPath() {
        return "issue318";
    }

    @Test
    public void testIssue318() {
        First f = new First();
        Assertions.notHasEnsuredPredicate(f);

        Second s = new Second(f);
        Assertions.notHasEnsuredPredicate(s);

        f.read();
        s.goOn();
        Assertions.hasEnsuredPredicate(f);
        Assertions.notHasEnsuredPredicate(s);

        Assertions.predicateErrors(1);
    }
}
