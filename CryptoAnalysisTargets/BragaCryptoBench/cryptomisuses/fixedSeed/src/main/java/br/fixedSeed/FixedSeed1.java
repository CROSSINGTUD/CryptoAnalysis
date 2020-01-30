package br.fixedSeed;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class FixedSeed1 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] pt1 = ("demo text").getBytes();
		byte[] seed = { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD,
				(byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD,
				(byte) 0xEF };

		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(128);
		Key k = g.generateKey();
		String[] aes = { "AES/OFB/NoPadding", "AES/CFB/NoPadding", "AES/CTR/NoPadding" };

		for (int a = 0; a < aes.length; a++) {
			Cipher enc = Cipher.getInstance(aes[a], "BC");
			Cipher dec = Cipher.getInstance(aes[a], "BC");
			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
				SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
				sr1.setSeed(seed);
				enc.init(Cipher.ENCRYPT_MODE, k, sr1);
				ct[i] = enc.doFinal(pt1);
				dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
				byte[] pt2 = dec.doFinal(ct[i]);
			}

			for (int i = 0; i < 2; i++) {
				SecureRandom sr2 = SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");
				sr2.setSeed(seed);
				enc.init(Cipher.ENCRYPT_MODE, k, sr2);
				ct[i] = enc.doFinal(pt1);
				dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
				byte[] pt2 = dec.doFinal(ct[i]);
			}
		}
	}
}
