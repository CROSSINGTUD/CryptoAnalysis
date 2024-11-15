package tests.error.forbiddenmethods;

import javax.crypto.spec.PBEKeySpec;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PBEKeySpecTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    @Test
    public void PBEKeySpecTest1() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {});
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }

    @Test
    public void PBEKeySpecTest2() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {}, new byte[1], 1000);
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }

    @Test
    public void PBEKeySpecTest3() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {}, new byte[1], 1000);
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }
}
