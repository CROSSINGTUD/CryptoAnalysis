package tests.jca;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SignatureTest extends UsagePatternTestingFramework {

	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");

	@Override
	protected String getRulesetPath() {
		return TestConstants.JCA_RULESET_PATH;
	}

	@Test
	public void testSignature2() throws GeneralSecurityException {
		Signature s = Signature.getInstance("SHA256withRSA");
		/*
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
	public void testSignature1() throws GeneralSecurityException {
		Signature s = Signature.getInstance("SHA256withRSA");
		// no initSign call
		s.update("".getBytes());
		s.sign();
		Assertions.notHasEnsuredPredicate(s);
		Assertions.mustNotBeInAcceptingState(s);
	}
	
	private static PrivateKey getPrivateKey() throws GeneralSecurityException {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		kpGen.initialize(4096);
		KeyPair gp = kpGen.generateKeyPair();
		return gp.getPrivate();
	}
	
	@Test
	public void positiveSignUsagePatternTest1() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(3072);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privateKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);
	}
	
	@Test
	public void negativeSignUsagePatternTest1() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);

		// Since 3.0.0: key size of 2048 is not allowed
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.notHasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.notHasEnsuredPredicate(privateKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.notHasEnsuredPredicate(signature);
	}
	
	@Test
	public void positiveSignUsagePatternTest2() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(3072);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privateKey);
		String algorithm = "SHA256withDSA";
		if (Math.random() % 2 == 0) {
			algorithm = "SHA256withECDSA";
		}
		Signature sign = Signature.getInstance(algorithm);
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);
	}
	
	@Test
	public void negativeSignUsagePatternTest2() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);

		// Since 3.0.0: key size of 2048 is not allowed
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.notHasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.notHasEnsuredPredicate(privateKey);
		String algorithm = "SHA256withDSA";
		if (Math.random() % 2 == 0) {
			algorithm = "SHA256withECDSA";
		}
		Signature sign = Signature.getInstance(algorithm);
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.notHasEnsuredPredicate(signature);
	}
	
	@Test
	public void positiveSignUsagePatternTest3() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(3072);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(privateKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);

		final PublicKey pubKey = kp.getPublic();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(pubKey);

		Signature ver = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		ver.initVerify(pubKey);
		ver.update(input.getBytes(StandardCharsets.UTF_8));
		ver.verify(signature);
		Assertions.mustBeInAcceptingState(ver);
	}
	
	@Test
	public void negativeSignUsagePatternTest3() throws GeneralSecurityException {
		String input = "TestTestTest";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		
		// Since 3.0.0: key size of 2048 is not allowed
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.notHasEnsuredPredicate(kp);

		final PrivateKey privateKey = kp.getPrivate();
		Assertions.notHasEnsuredPredicate(privateKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privateKey);
		sign.update(input.getBytes(StandardCharsets.UTF_8));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.notHasEnsuredPredicate(signature);

		final PublicKey pubKey = kp.getPublic();
		Assertions.notHasEnsuredPredicate(pubKey);

		Signature ver = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		ver.initVerify(pubKey);
		ver.update(input.getBytes(StandardCharsets.UTF_8));
		ver.verify(signature);
		Assertions.mustBeInAcceptingState(ver);
	}
	
}
