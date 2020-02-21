
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

public final class UseRandomIVsForCFB128 {
	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] pt1 = ("demo text").getBytes();

		byte[] iv = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(iv);

		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(256);
		Key k = g.generateKey();
		Cipher enc = Cipher.getInstance("AES/CFB128/NoPadding", "BC");
		Cipher dec = Cipher.getInstance("AES/CFB128/NoPadding", "BC");
		byte[] ct;
		for (int i = 0; i < 5; i++) {
			SecureRandom.getInstanceStrong().nextBytes(iv);
			enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
			ct = enc.doFinal(pt1);
			dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
			byte[] pt2 = dec.doFinal(ct);

		}
	}
}
