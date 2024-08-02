package tests.issues.issue314;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class Issue314Test extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "issue314";
    }

    @Test
    public void testIssue314() {
        Thread thread = new Thread();
        thread.start();

        Assertions.incompleteOperationErrors(0);
    }
}
