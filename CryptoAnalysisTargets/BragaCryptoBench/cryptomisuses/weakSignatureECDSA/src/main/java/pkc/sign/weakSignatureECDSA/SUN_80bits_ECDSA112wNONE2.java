package pkc.sign.weakSignatureECDSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_80bits_ECDSA112wNONE2 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(112, new SecureRandom());
		Signature sign = Signature.getInstance("NONEwithECDSA", "SunEC");

		KeyPair kp = kpg.generateKeyPair();

		sign.initSign(kp.getPrivate(), new SecureRandom());
		byte[] doc = "demo doc".getBytes();
		MessageDigest md1 = MessageDigest.getInstance("SHA-256", "SUN");
		md1.update(doc);
		byte[] hash = md1.digest();
		sign.update(hash);
		byte[] signed = sign.sign();

		Signature verifier = Signature.getInstance("NONEwithECDSA", "SunEC");

		MessageDigest md2 = MessageDigest.getInstance("SHA-256", "SUN");
		md2.update(doc);
		byte[] hash2 = md2.digest();
		verifier.initVerify(kp.getPublic());
		verifier.update(hash2);
		boolean ok = verifier.verify(signed);

	}
}
