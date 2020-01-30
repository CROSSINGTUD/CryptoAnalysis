package pkm.constantKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class ConstantKeyAES3 {

	public static void main(String[] a) {
		try {
			byte[] ck = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF".getBytes();
			byte[] iv = new byte[16];
			(new SecureRandom()).nextBytes(iv);
			byte[] msg = "demo text".getBytes();
			SecretKeySpec ks = new SecretKeySpec(ck, "AES");
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
			AlgorithmParameterSpec aps = new IvParameterSpec(iv);
			c.init(Cipher.ENCRYPT_MODE, ks, aps);
			byte[] ct = c.doFinal(msg);
			SecretKeySpec ks1 = new SecretKeySpec(ks.getEncoded(), "AES");
			c.init(Cipher.DECRYPT_MODE, ks1, aps);
			byte[] pt = c.doFinal(ct);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| NoSuchProviderException e) {
		}
	}
}
