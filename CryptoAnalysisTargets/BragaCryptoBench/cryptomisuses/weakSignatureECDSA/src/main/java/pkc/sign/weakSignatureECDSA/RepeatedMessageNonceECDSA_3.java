package pkc.sign.weakSignatureECDSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.util.Arrays;

public final class RepeatedMessageNonceECDSA_3 {

	public static void main(String[] args) throws Exception {

		ECGenParameterSpec ecps = new ECGenParameterSpec("secp256r1");

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(ecps);

		KeyPair kp = kpg.generateKeyPair();

		byte[] seed = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };

		SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
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
