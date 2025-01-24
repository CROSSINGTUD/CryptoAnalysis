package tests.jca;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;
import org.junit.Ignore;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class KeyPairTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    @Ignore("Requires to resolve the static field F4")
    @Test
    public void positiveRsaParameterSpecTest() throws GeneralSecurityException {
        int keySize = 4096;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec parameters =
                new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(parameters);
        generator.initialize(parameters, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        Assertions.hasEnsuredPredicate(keyPair);
    }

    @Test
    public void negativeRsaParameterSpecTest() throws GeneralSecurityException {
        // Since 3.0.0: key size of 2048 is not allowed
        int keySize = 2048;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec parameters =
                new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
        Assertions.notHasEnsuredPredicate(parameters);
        Assertions.extValue(0);
        Assertions.extValue(1);
        generator.initialize(parameters, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        Assertions.notHasEnsuredPredicate(keyPair);
    }

    @Test
    public void positiveRsaParameterSpecTestBigInteger() throws GeneralSecurityException {
        int keySize = 4096;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec parameters =
                new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(65537));
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(parameters);
        generator.initialize(parameters, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        Assertions.hasEnsuredPredicate(keyPair);
    }

    @Test
    public void negativeRsaParameterSpecTestBigInteger() throws GeneralSecurityException {
        // Since 3.0.0: key size of 2048 is not allowed
        int keySize = 2048;
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec parameters =
                new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(65537));
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.notHasEnsuredPredicate(parameters);
        generator.initialize(parameters, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        Assertions.notHasEnsuredPredicate(keyPair);
    }
}
