
package Crypto;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/** @author CogniCrypt */
public class Enc {

	public byte[] encrypt(byte[] data, SecretKey key) throws GeneralSecurityException {

		byte[] ivb = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(ivb);
		IvParameterSpec iv = new IvParameterSpec(ivb);

		Cipher c = Cipher.getInstance("AES/CFB/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, key, iv);

		byte[] res = c.doFinal(data);

		byte[] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, res.length);

		return ret;

	}

	public byte[] decrypt(byte[] ciphertext, SecretKey key) throws GeneralSecurityException {

		byte[] ivb = new byte[16];
		System.arraycopy(ciphertext, 0, ivb, 0, ivb.length);
		IvParameterSpec iv = new IvParameterSpec(ivb);
		byte[] data = new byte[ciphertext.length - ivb.length];
		System.arraycopy(ciphertext, ivb.length, data, 0, data.length);

		Cipher c = Cipher.getInstance("AES/CFB/NoPadding");
		c.init(Cipher.DECRYPT_MODE, key, iv);

		byte[] res = c.doFinal(data);

		return res;

	}
}
