package wc.riskyInsecureCrypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.SecureRandom;

public final class InsecureCrypto3DES {

	public static void main(String[] a) {

		try {
			byte[] msg = "msg demo".getBytes();
			byte[] iv = new byte[8];
			(new SecureRandom()).nextBytes(iv);
			KeyGenerator kg = KeyGenerator.getInstance("DESede", "SunJCE");
			kg.init(168);
			Key k = kg.generateKey();
			Cipher c = Cipher.getInstance("DESede/CTR/NoPadding", "SunJCE");
			AlgorithmParameterSpec aps = new IvParameterSpec(iv);
			c.init(Cipher.ENCRYPT_MODE, k, aps);
			byte[] ct = c.doFinal(msg);
			iv = c.getIV();
			c.init(Cipher.DECRYPT_MODE, k, aps);
			byte[] pt = c.doFinal(ct);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			System.out.println(e);
		}
	}
}
