package tests.jca;

import java.security.GeneralSecurityException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.PBEKeySpec;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ExtractValueTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    @Test
    public void testInterProceduralStringFlow() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance(getAES());
        Assertions.extValue(0);
        keygen.init(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void charArrayExtractionTest() {
        char[] v = new char[] {'p'};
        final PBEKeySpec pbekeyspec = new PBEKeySpec(v, null, 65000, 128);
        Assertions.extValue(0);
        Assertions.notHasEnsuredPredicate(pbekeyspec);
    }

    @Test
    public void testIntraProceduralStringFlow() throws GeneralSecurityException {
        String aes = "AES";
        KeyGenerator keygen = KeyGenerator.getInstance(aes);
        Assertions.extValue(0);
        keygen.init(0);
    }

    @Test
    public void testInterProceduralStringFlowDirect() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance(getAESReturn());
        Assertions.extValue(0);
        keygen.init(0);
    }

    @Test
    public void testIntraProceduralIntFlowDirect() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        int val = 0;
        keygen.init(val);
        Assertions.extValue(0);
    }

    @Test
    public void testIntraProceduralNativeNoCalleeIntFlow() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        int val = noCallee();
        keygen.init(val);
        Assertions.extValue(0);
    }

    private String getAESReturn() {
        return "AES";
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private String getAES() {
        String var = "AES";
        return var;
    }

    private static native int noCallee();
}
