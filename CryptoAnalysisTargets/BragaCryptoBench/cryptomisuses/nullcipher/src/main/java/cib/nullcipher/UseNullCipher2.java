package cib.nullcipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseNullCipher2 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] pt1 = ("demo text").getBytes();

		byte[] iv = new byte[16];
		(new SecureRandom()).nextBytes(iv);

		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(128);
		Key k = g.generateKey();
		String[] aes = { "AES/OFB/NoPadding", "AES/CFB/NoPadding", "AES/CTR/NoPadding" };
		boolean fixIV = true;
		for (int a = 0; a < aes.length; a++) {
			Cipher enc = new NullCipher();
			Cipher dec = new javax.crypto.NullCipher();
			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
				enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
				ct[i] = enc.doFinal(pt1);
				dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
				byte[] pt2 = dec.doFinal(ct[i]);
				if (!fixIV) {
					iv[iv.length - 1] = (byte) (iv[iv.length - 1] ^ 0x01);
				}
			}
		}
	}
}
