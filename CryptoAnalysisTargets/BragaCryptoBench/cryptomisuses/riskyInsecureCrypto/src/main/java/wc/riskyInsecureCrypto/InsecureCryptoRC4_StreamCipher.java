package wc.riskyInsecureCrypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.*;

public final class InsecureCryptoRC4_StreamCipher {

	public static void main(String[] a) {
		try {
			Cipher cf = Cipher.getInstance("ARCFOUR/ECB/NoPadding", "SunJCE");
			KeyGenerator kg = KeyGenerator.getInstance("ARCFOUR", "SunJCE");
			kg.init(128);
			Key k = kg.generateKey();
			byte[] msg = "demo msg".getBytes();

			cf.init(Cipher.ENCRYPT_MODE, k);
			byte[] ct = cf.doFinal(msg);
			cf.init(Cipher.DECRYPT_MODE, k);
			byte[] pt = cf.doFinal(ct);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
		}
	}
}
