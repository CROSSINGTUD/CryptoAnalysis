package tests.custom.issue318;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class Issue318Test extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "issue318";
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
