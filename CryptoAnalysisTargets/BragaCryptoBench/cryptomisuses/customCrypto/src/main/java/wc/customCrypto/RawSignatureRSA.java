package wc.customCrypto;

import java.security.*;

public final class RawSignatureRSA {

	public static void main(String args[]) {
		byte[] msg = "demo msg".getBytes();
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunJSSE");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			Signature sig = Signature.getInstance("SHA1WithRSA", "SunJSSE");
			sig.initSign(kp.getPrivate());
			sig.update(msg);
			byte[] signed = sig.sign();
			sig.initVerify(kp.getPublic());
			sig.update(msg);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
		}
	}
}
