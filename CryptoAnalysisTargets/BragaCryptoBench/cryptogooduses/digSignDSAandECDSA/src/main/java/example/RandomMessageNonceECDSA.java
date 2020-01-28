package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.util.Arrays;

public final class RandomMessageNonceECDSA {

	public static void main(String[] args) throws Exception {

		ECGenParameterSpec ecps = new ECGenParameterSpec("secp256r1");

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(ecps);

		KeyPair kp = kpg.generateKeyPair();

		SecureRandom sr1 = SecureRandom.getInstanceStrong();
		byte[] seed = sr1.generateSeed(24);
		sr1.setSeed(seed);

		Signature signer1 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer1.initSign(kp.getPrivate(), sr1);
		byte[] doc = "this is a demo text".getBytes();
		signer1.update(doc);
		byte[] sign1 = signer1.sign();

		SecureRandom sr2 = SecureRandom.getInstanceStrong();
		sr2.setSeed(seed);

		Signature signer2 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer2.initSign(kp.getPrivate(), sr2);
		doc = "this is a demo text".getBytes();
		signer2.update(doc);
		byte[] sign2 = signer2.sign();

		boolean ok = Arrays.areEqual(sign1, sign2);
	}
}
