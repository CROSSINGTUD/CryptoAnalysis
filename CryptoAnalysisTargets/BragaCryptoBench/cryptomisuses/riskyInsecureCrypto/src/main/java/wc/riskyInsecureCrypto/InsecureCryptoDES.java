package wc.riskyInsecureCrypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

public final class InsecureCryptoDES {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
		byte[] msg = "msg demo".getBytes();
		KeyGenerator kg = KeyGenerator.getInstance("DES", "SunJCE");
		kg.init(56);
		byte[] iv = new byte[8];
		(new SecureRandom()).nextBytes(iv);
		Key k = kg.generateKey();
		Cipher c = Cipher.getInstance("DES/CTR/NoPadding", "SunJCE");
		AlgorithmParameterSpec aps = new IvParameterSpec(iv);
		c.init(Cipher.ENCRYPT_MODE, k, aps);
		byte[] ct = c.doFinal(msg);
		c.init(Cipher.DECRYPT_MODE, k);
		byte[] pt = c.doFinal(ct);
	}
}
