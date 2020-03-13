package pkc.sign.weakSignatureECDSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import org.bouncycastle.util.Arrays;

public final class RepeatedMessageNonceECDSA_1 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(256, SecureRandom.getInstanceStrong());

		KeyPair kp = kpg.generateKeyPair();

		SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] seed = sr1.generateSeed(24);
		sr1.setSeed(seed);

		Signature signer1 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer1.initSign(kp.getPrivate(), sr1);
		byte[] doc = "demo doc".getBytes();
		signer1.update(doc);
		byte[] sign1 = signer1.sign();

		SecureRandom sr2 = SecureRandom.getInstance("SHA1PRNG", "SUN");
		sr2.setSeed(seed);

		Signature signer2 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer2.initSign(kp.getPrivate(), sr2);
		doc = "demo doc".getBytes();
		signer2.update(doc);
		byte[] sign2 = signer2.sign();

		boolean ok = Arrays.areEqual(sign1, sign2);

	}
}
