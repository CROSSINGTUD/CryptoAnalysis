package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_112bits_ECDSA224wSHA224 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(224, new SecureRandom());
		Signature signer = Signature.getInstance("SHA224WithECDSA", "SunEC");

		KeyPair kp = kpg.generateKeyPair();

		signer.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "this is a demo text".getBytes();
		signer.update(doc);
		byte[] signed = signer.sign();

		Signature verifier = Signature.getInstance("SHA224WithECDSA", "SunEC");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);

	}
}
