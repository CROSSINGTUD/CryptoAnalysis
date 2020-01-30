package pkm.constantKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class ConstantKey3DES {

	public static void main(String[] a) {
		try {
			byte[] ck = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF".getBytes();
			byte[] iv = new byte[8];
			(new SecureRandom()).nextBytes(iv);
			byte[] msg = "demo text".getBytes();
			KeySpec ks = new DESedeKeySpec(ck);
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede", "SunJCE");
			SecretKey k = kf.generateSecret(ks);
			Cipher c = Cipher.getInstance("DESede/CTR/NoPadding", "SunJCE");
			AlgorithmParameterSpec aps = new IvParameterSpec(iv);
			c.init(Cipher.ENCRYPT_MODE, k, aps);
			byte[] ct = c.doFinal(msg);
			c.init(Cipher.DECRYPT_MODE, k, aps);
			byte[] pt = c.doFinal(ct);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| NoSuchProviderException e) {
		}
	}
}
