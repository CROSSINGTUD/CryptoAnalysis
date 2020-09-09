package tests.pattern;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SignatureTest extends UsagePatternTestingFramework {

	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");
	
	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void testSignature2() throws InvalidKeyException, GeneralSecurityException {
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
	
	@Test
	public void testSignature1() throws InvalidKeyException, GeneralSecurityException {
		Signature s = Signature.getInstance("SHA256withRSA");
		// no initSign call
		s.update("".getBytes());
		s.sign();
		Assertions.notHasEnsuredPredicate(s);
		Assertions.mustNotBeInAcceptingState(s);
	}
	
	private static PrivateKey getPrivateKey() throws GeneralSecurityException {
		KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
		kpgen.initialize(2048);
		KeyPair gp = kpgen.generateKeyPair();
		return gp.getPrivate();
	}
	
	@Test
	public void signUsagePatternTest1() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);
	}
	
	@Test
	public void signUsagePatternTest2() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privKey);
		String algorithm = "SHA256withDSA";
		if (Math.random() % 2 == 0) {
			algorithm = "SHA256withECDSA";
		}
		Signature sign = Signature.getInstance(algorithm);
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);
	}
	
	@Test
	public void signUsagePatternTest3() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(privKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);

		final PublicKey pubKey = kp.getPublic();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(pubKey);

		Signature ver = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);
		//
		ver.initVerify(pubKey);
		ver.update(input.getBytes("UTF-8"));
		ver.verify(signature);
		Assertions.mustBeInAcceptingState(ver);
	}
	
}
