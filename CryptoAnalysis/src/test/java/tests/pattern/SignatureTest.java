package tests.pattern;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SignatureTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");
	
	@Test
	public void testSignature() throws InvalidKeyException, GeneralSecurityException {
		Signature s = Signature.getInstance("SHA256withRSA");
		/**
		 * The Signature API expects a call to update here. This call supplied the actual data to the signature instance.
		 * A call such as s.update(data); would resolve this finding.
		 */
		s.initSign(getPrivateKey());
		s.update(tData);
		s.sign();
		Assertions.notHasEnsuredPredicate(s); // passing
		Assertions.mustBeInAcceptingState(s); 
	}
	
	private static PrivateKey getPrivateKey() throws GeneralSecurityException {
		KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
		kpgen.initialize(2048);
		KeyPair gp = kpgen.generateKeyPair();
		return gp.getPrivate();
	}
}
