package tests.pattern;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SecureRandomTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void corSeed() throws GeneralSecurityException {
		SecureRandom r3 = SecureRandom.getInstanceStrong();
		Assertions.hasEnsuredPredicate(r3);
		
		SecureRandom r4 = SecureRandom.getInstanceStrong();
		Assertions.hasEnsuredPredicate(r4);
		r4.setSeed(r3.nextInt());
	}
	
	@Test
	public void fixedSeed() throws GeneralSecurityException {
		final int fixedSeed = 10;
		SecureRandom r3 = SecureRandom.getInstanceStrong();
		r3.setSeed(fixedSeed);
		Assertions.notHasEnsuredPredicate(r3);
		
		SecureRandom r4 = SecureRandom.getInstanceStrong();
		Assertions.notHasEnsuredPredicate(r4);
		r4.setSeed(r3.nextInt());
	}

	@Test
	public void dynSeed() throws GeneralSecurityException {
		SecureRandom srPrep = new SecureRandom();
		byte[] bytes = new byte[32];
		srPrep.nextBytes(bytes);
		Assertions.mustBeInAcceptingState(srPrep);
		Assertions.hasEnsuredPredicate(bytes);
		// sr.setSeed(456789L); // Noncompliant

		SecureRandom sr = new SecureRandom();
		sr.setSeed(bytes);
		int v = sr.nextInt();
		Assertions.hasEnsuredPredicate(v);
		Assertions.mustBeInAcceptingState(sr);
	}

	@Test
	public void staticSeed() throws GeneralSecurityException {
		byte[] bytes = {(byte) 100, (byte) 200};
		SecureRandom sr = new SecureRandom();
		sr.setSeed(bytes);
		int v = sr.nextInt();
		Assertions.notHasEnsuredPredicate(v);
		Assertions.mustBeInAcceptingState(sr);
	}

}
