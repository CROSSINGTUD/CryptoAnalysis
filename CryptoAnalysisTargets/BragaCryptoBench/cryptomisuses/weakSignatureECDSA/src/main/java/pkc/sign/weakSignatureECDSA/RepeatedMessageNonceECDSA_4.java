package pkc.sign.weakSignatureECDSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.util.Arrays;

public final class RepeatedMessageNonceECDSA_4 {

	public static void main(String[] args) throws Exception {

		ECGenParameterSpec ecps = new ECGenParameterSpec("secp256r1");

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		kpg.initialize(ecps);

		KeyPair kp = kpg.generateKeyPair();

		SecureRandom sr1 = new SecureRandom();
		byte[] seed = sr1.generateSeed(24);
		sr1.setSeed(seed);

		Signature signer1 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer1.initSign(kp.getPrivate(), sr1);
		byte[] doc = "demo doc".getBytes();
		signer1.update(doc);
		byte[] sign1 = signer1.sign();

		SecureRandom sr2 = new SecureRandom();
		sr2.setSeed(seed);

		Signature signer2 = Signature.getInstance("SHA256withECDSA", "SunEC");
		signer2.initSign(kp.getPrivate(), sr2);
		doc = "demo doc".getBytes();
		signer2.update(doc);
		byte[] sign2 = signer2.sign();

		boolean ok = Arrays.areEqual(sign1, sign2);

	}
}
