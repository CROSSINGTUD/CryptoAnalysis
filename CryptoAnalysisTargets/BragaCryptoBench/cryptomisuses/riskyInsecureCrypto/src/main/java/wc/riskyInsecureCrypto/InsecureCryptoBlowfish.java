package wc.riskyInsecureCrypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

public final class InsecureCryptoBlowfish {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
		byte[] msg = "This is a test Blowfsh".getBytes();

		KeyGenerator kg = KeyGenerator.getInstance("Blowfish", "SunJCE");
		kg.init(128);
		byte[] iv = new byte[8];
		(new SecureRandom()).nextBytes(iv);
		Key k = kg.generateKey();
		Cipher c = Cipher.getInstance("Blowfish/CTR/NoPadding", "SunJCE");
		AlgorithmParameterSpec aps = new IvParameterSpec(iv);
		c.init(Cipher.ENCRYPT_MODE, k, aps);
		byte[] ct = c.doFinal(msg);

		c.init(Cipher.DECRYPT_MODE, k, aps);
		byte[] pt = c.doFinal(ct);
	}

}
