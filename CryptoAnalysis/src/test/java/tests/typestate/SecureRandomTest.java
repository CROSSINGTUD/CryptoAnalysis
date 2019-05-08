package tests.typestate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Ignore;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class SecureRandomTest extends IDEALCrossingTestingFramework{


	@Override
	protected Ruleset getRuleset() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Override
	protected String getRulename() {
		return "SecureRandom";
	}
	
	@Ignore
	@Test
	public void testSecureRandom1() throws NoSuchAlgorithmException {
		final byte[] salt = new byte[32];
		SecureRandom sr = SecureRandom.getInstanceStrong();
		sr.nextBytes(salt);
		Assertions.assertState(sr, 2);
	}
	@Test
	public void testSecureRandom2() throws NoSuchAlgorithmException {
		final byte[] salt = new byte[32];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		Assertions.assertState(sr, 2);
	}
	@Test
	public void testSecureRandom3() throws NoSuchAlgorithmException {
		SecureRandom sr = new SecureRandom();
		Assertions.assertState(sr, 0);
	}
}
