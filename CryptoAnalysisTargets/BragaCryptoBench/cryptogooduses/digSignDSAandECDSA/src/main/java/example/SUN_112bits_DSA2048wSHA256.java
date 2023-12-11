package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_112bits_DSA2048wSHA256 {

	/**
	 * Original test with updated constraints
	 *	kpg.initialize(2048, new SecureRandom()) -> kpg.initialize(3072, new SecureRandom()); 	
	 */
	public void positiveTestCase() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "SUN");
		kpg.initialize(3072, new SecureRandom());
		Signature sign1 = Signature.getInstance("SHA256withDSA", "SUN");

		KeyPair kp = kpg.generateKeyPair();

		sign1.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "this is a demo text".getBytes();
		sign1.update(doc);
		byte[] signed = sign1.sign();

		Signature verifier = Signature.getInstance("SHA256withDSA", "SUN");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);
	}

	/**
	 * Original test without any updates
	 */
	public void negativeTestCase() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "SUN");

		// Since 3.0.0: key size of 2048 is not allowed
		kpg.initialize(2048, new SecureRandom());
		Signature sign1 = Signature.getInstance("SHA256withDSA", "SUN");

		KeyPair kp = kpg.generateKeyPair();

		sign1.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "this is a demo text".getBytes();
		sign1.update(doc);
		byte[] signed = sign1.sign();

		Signature verifier = Signature.getInstance("SHA256withDSA", "SUN");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);
	}
}
