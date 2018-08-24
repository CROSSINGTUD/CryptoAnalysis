package issue49;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;

public class Main {
	public byte[] sign(String data) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(getPrivateKey());
		signature.update(data.getBytes());
		return signature.sign();
	}

	private PrivateKey getPrivateKey() throws NoSuchAlgorithmException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("AES");
		gen.initialize(1024);
		KeyPair keyPair = gen.generateKeyPair();
		return keyPair.getPrivate();
	}
}
