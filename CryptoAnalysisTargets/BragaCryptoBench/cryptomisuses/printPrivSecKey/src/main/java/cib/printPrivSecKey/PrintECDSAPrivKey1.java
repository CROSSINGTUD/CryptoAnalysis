package cib.printPrivSecKey;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class PrintECDSAPrivKey1 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(571, new SecureRandom());
		Signature sign = Signature.getInstance("SHA512WithECDSA", "SunEC");

		KeyPair kp = kpg.generateKeyPair();

		sign.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "demo text".getBytes();
		sign.update(doc);
		byte[] signed = sign.sign();

		Signature verifier = Signature.getInstance("SHA512WithECDSA", "SunEC");

		verifier.initVerify(kp.getPublic());
		verifier.update(doc);
		boolean ok = verifier.verify(signed);

	}
}
