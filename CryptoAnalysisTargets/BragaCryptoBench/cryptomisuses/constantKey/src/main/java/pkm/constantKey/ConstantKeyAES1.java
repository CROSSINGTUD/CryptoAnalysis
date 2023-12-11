package pkm.constantKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class ConstantKeyAES1 {

	public static void main(String[] a) {
		try {
			byte[] ck = { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD,
					(byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
					(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
					(byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
					(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
			byte[] iv = new byte[16];
			(new SecureRandom()).nextBytes(iv);
			byte[] msg = "demo text".getBytes();
			SecretKeySpec ks = new SecretKeySpec(ck, "AES");
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
			AlgorithmParameterSpec aps = new IvParameterSpec(iv);
			c.init(Cipher.ENCRYPT_MODE, ks, aps);
			byte[] ct = c.doFinal(msg);
			c.init(Cipher.DECRYPT_MODE, ks, aps);
			byte[] pt = c.doFinal(ct);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| NoSuchProviderException e) {
		}
	}
}
