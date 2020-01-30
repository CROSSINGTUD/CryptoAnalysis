package wc.customCrypto;

import javax.crypto.Cipher;
import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class RawSignatureRSAwHash {

	public static void main(String args[]) {
		byte[] msg = "demo msg".getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512", "SUN");
			md.update(msg);
			byte[] digested = md.digest();
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunJSSE");
			kpg.initialize(3072);
			KeyPair kp = kpg.generateKeyPair();
			Cipher ciph = Cipher.getInstance("RSA", "SunJCE");
			ciph.init(Cipher.ENCRYPT_MODE, kp.getPrivate());
			byte[] ciphered = ciph.doFinal(digested);

			byte[] hashDigested = md.digest(msg);
			ciph.init(Cipher.DECRYPT_MODE, kp.getPublic());
			byte[] hashOriginal = ciph.doFinal(ciphered);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
		}
	}
}
