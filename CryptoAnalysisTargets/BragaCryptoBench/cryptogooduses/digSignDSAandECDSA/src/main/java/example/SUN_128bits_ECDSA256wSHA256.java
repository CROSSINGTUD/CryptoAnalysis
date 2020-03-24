package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_128bits_ECDSA256wSHA256 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(256, new SecureRandom());
		Signature sing = Signature.getInstance("SHA256WithECDSA", "SunEC");

		KeyPair kp = kpg.generateKeyPair();

		sing.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "this is a demo text".getBytes();
		sing.update(doc);
		byte[] signed = sing.sign();

		Signature verifier = Signature.getInstance("SHA256WithECDSA", "SunEC");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);

	}
}
