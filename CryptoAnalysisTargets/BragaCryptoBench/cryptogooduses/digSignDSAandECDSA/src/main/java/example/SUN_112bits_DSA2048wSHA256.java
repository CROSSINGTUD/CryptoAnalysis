package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_112bits_DSA2048wSHA256 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "SUN");
		kpg.initialize(2048, new SecureRandom());
		Signature sign1 = Signature.getInstance("SHA256WithDSA", "SUN");

		KeyPair kp = kpg.generateKeyPair();

		sign1.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "this is a demo text".getBytes();
		sign1.update(doc);
		byte[] signed = sign1.sign();

		Signature verifier = Signature.getInstance("SHA256WithDSA", "SUN");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);

	}
}
