package tests.jca;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.junit.Ignore;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PBETest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    @Test
    public void predictablePassword() {
        char[] defaultKey = new char[] {'s', 'a', 'a', 'g', 'a', 'r'};
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(defaultKey, salt, 11010, 16);
        Assertions.hasNotGeneratedPredicate(pbeKeySpec);
        pbeKeySpec.clearPassword();
        Assertions.mustBeInAcceptingState(pbeKeySpec);
    }

    @Test
    public void unPredictablePassword() {
        char[] defaultKey = generateRandomPassword();
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);

        PBEKeySpec pbeKeySpec = new PBEKeySpec(defaultKey, salt, 11010, 16);
        Assertions.hasEnsuredPredicate(pbeKeySpec);
        pbeKeySpec.clearPassword();
        Assertions.mustBeInAcceptingState(pbeKeySpec);
    }

    @Test
    public void pbeUsagePatternMinPBEIterationsMinimized() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] corPwd = generateRandomPassword();
        PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(pbekeyspec);
    }

    @Test
    public void pbeUsagePatternMinPBEIterations() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] corPwd = generateRandomPassword();
        PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasGeneratedPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);
        pbekeyspec = new PBEKeySpec(corPwd, salt, 9999, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasNotGeneratedPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);

        PBEParameterSpec pbeParSpec1 = new PBEParameterSpec(salt, 10000);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.mustBeInAcceptingState(pbeParSpec1);
        Assertions.hasEnsuredPredicate(pbeParSpec1);

        PBEParameterSpec pbeParSpec2 = new PBEParameterSpec(salt, 9999);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.hasNotGeneratedPredicate(pbeParSpec2);
        Assertions.mustBeInAcceptingState(pbeParSpec2);
    }

    @Test
    public void pbeUsagePattern1() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        Assertions.hasEnsuredPredicate(salt);
        char[] corPwd = generateRandomPassword();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasEnsuredPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
    }

    @Ignore(
            "Typestate analysis returns state 0 and 1 after call to clearPassword() (should only be 1)")
    @Test
    public void pbeUsagePattern2() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);
        Assertions.hasEnsuredPredicate(salt);
        final PBEKeySpec pbekeyspec = new PBEKeySpec(generateRandomPassword(), salt, 65000, 128);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        Assertions.hasEnsuredPredicate(pbekeyspec);

        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);
        Assertions.notHasEnsuredPredicate(pbekeyspec);
    }

    public char[] generateRandomPassword() {
        SecureRandom rnd = new SecureRandom();

        return IntStream.generate(() -> rnd.nextInt('a', 'z'))
                .mapToObj(Character::toString)
                .limit(10)
                .collect(Collectors.joining())
                .toCharArray();
    }

    @Test
    public void pbeUsagePatternForbiddenMeth() {
        char[] falsePwd = "password".toCharArray();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd);
        Assertions.callToForbiddenMethod();
        Assertions.notHasEnsuredPredicate(pbekeyspec);
    }
}
