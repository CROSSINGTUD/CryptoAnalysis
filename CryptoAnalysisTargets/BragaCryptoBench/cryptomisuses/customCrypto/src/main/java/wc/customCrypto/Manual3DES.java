package wc.customCrypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class Manual3DES {

	public static void main(String[] a) {

		try {
			String k1 = "0123456789ABCDEF";
			String k2 = "1123456789ABCDEF";
			String k3 = "2123456789ABCDEF";
			byte[] k123 = (k1 + k2 + k3).getBytes();
			byte[] iv = null;
			byte[] msg = "demo msg".getBytes();
			KeySpec ks = new DESedeKeySpec(k123);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede", "SunJCE");
			SecretKey k = kf.generateSecret(ks);
			Cipher c = Cipher.getInstance("DESede", "SunJCE");
			c.init(Cipher.ENCRYPT_MODE, k);
			byte[] theCph = c.doFinal(msg);
			c.init(Cipher.DECRYPT_MODE, k);
			byte[] theClear = c.doFinal(theCph);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
		}
	}

}
