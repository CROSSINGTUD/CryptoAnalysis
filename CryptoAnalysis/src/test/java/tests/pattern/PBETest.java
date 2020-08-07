package tests.pattern;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PBETest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void predictablePassword() throws GeneralSecurityException {
		char[] defaultKey = new char[] {'s', 'a', 'a', 'g', 'a', 'r'};
		byte[] salt = new byte[16];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(defaultKey, salt, 11010, 16);
		Assertions.notHasEnsuredPredicate(pbeKeySpec);
		pbeKeySpec.clearPassword();
		Assertions.mustBeInAcceptingState(pbeKeySpec);
	}

	@Test
	public void unPredictablePassword() throws GeneralSecurityException {
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
	public void pbeUsagePatternMinPBEIterationsMinimized() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = generateRandomPassword();;
		PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
		Assertions.extValue(1);
	}
	
	@Test
	public void pbeUsagePatternMinPBEIterations() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = generateRandomPassword();
		PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();
		pbekeyspec = new PBEKeySpec(corPwd, salt, 9999, 128);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.notHasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();

		PBEParameterSpec pbeparspec = new PBEParameterSpec(salt, 10000);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustBeInAcceptingState(pbeparspec);
		Assertions.hasEnsuredPredicate(pbeparspec);

		pbeparspec = new PBEParameterSpec(salt, 9999);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustBeInAcceptingState(pbeparspec);
		Assertions.notHasEnsuredPredicate(pbeparspec);
	}
	
	@Test
	public void pbeUsagePattern1() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		Assertions.hasEnsuredPredicate(salt);
		char[] corPwd = generateRandomPassword();;
		final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
		// Assertions.violatedConstraint(pbekeyspec);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();
	}
	
	@Test
	public void pbeUsagePattern2() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		Assertions.hasEnsuredPredicate(salt);
		final PBEKeySpec pbekeyspec = new PBEKeySpec(generateRandomPassword(), salt, 65000, 128);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		pbekeyspec.clearPassword();
		Assertions.notHasEnsuredPredicate(pbekeyspec);
	}
	
	public char[] generateRandomPassword() {
		SecureRandom rnd = new SecureRandom();
		char[] defaultKey = new char[20];
		for (int i = 0; i < 20; i++) {
			defaultKey[i] = (char) (rnd.nextInt(26) + 'a');
		}
		return defaultKey;
	}
	
	@Test
	public void pbeUsagePatternForbMeth() throws GeneralSecurityException, IOException {
		char[] falsePwd = "password".toCharArray();
		final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd);
		Assertions.callToForbiddenMethod();
	}

}
