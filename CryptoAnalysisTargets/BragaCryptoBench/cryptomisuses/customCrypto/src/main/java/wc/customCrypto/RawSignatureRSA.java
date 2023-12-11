package wc.customCrypto;

import java.security.*;

public final class RawSignatureRSA {

	/**
	 * Original test with updated constraints:
	 * 	kg.initialize(2048, ...) -> kg.initialize(4096, ...)
	 */
	public void positiveTestCase() {
		byte[] msg = "demo msg".getBytes();
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunJSSE");
			kpg.initialize(4096);
			KeyPair kp = kpg.generateKeyPair();
			Signature sig = Signature.getInstance("SHA1WithRSA", "SunJSSE");
			sig.initSign(kp.getPrivate());
			sig.update(msg);
			byte[] signed = sig.sign();
			sig.initVerify(kp.getPublic());
			sig.update(msg);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
		}
	}

	/**
	 * Original test without updates
	 */
	public void negativeTestCase() {
		byte[] msg = "demo msg".getBytes();
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunJSSE");

			// Since 3.0.0: key size of 2048 is not allowed
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			Signature sig = Signature.getInstance("SHA1WithRSA", "SunJSSE");
			sig.initSign(kp.getPrivate());
			sig.update(msg);
			byte[] signed = sig.sign();
			sig.initVerify(kp.getPublic());
			sig.update(msg);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
		}
	}
}
