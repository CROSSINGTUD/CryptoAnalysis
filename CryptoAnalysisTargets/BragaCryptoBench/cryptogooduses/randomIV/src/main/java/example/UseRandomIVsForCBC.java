package example;

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

public final class UseRandomIVsForCBC {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] pt1 = ("static counter").getBytes();

		byte[] iv = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(iv);

		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(128);
		Key k = g.generateKey();

		Cipher enc = Cipher.getInstance("AES/CBC/NoPadding", "BC");
		Cipher dec = Cipher.getInstance("AES/CBC/NoPadding", "BC");
		byte[] ct;

		enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
		ct = enc.doFinal(pt1);
		byte[] iv2 = enc.getIV();
		dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv2));
		byte[] pt2 = dec.doFinal(ct);

		enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
		ct = enc.doFinal(pt1);
		iv2 = enc.getIV();
		dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv2));
		pt2 = dec.doFinal(ct);

	}
}
