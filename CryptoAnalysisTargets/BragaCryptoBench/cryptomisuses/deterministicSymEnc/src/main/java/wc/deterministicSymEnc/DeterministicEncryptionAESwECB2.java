package wc.deterministicSymEnc;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class DeterministicEncryptionAESwECB2 {

	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] text1 = ("demo text").getBytes();
		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(256);
		Key k = g.generateKey();
		String[] aes = { "AES/ECB/NoPadding", "AES/ECB/PKCS5Padding", "AES/ECB/PKCS7Padding" };
		for (int a = 0; a < aes.length; a++) {
			Cipher enc = Cipher.getInstance(aes[a], "BC");
			enc.init(Cipher.ENCRYPT_MODE, k);
			Cipher dec = Cipher.getInstance(aes[a], "BC");
			dec.init(Cipher.DECRYPT_MODE, k);

			byte[][] bytearr = new byte[2][];
			for (int i = 0; i < 2; i++) {
				bytearr[i] = enc.doFinal(text1);
				byte[] decrypted = dec.doFinal(bytearr[i]);
			}
		}
	}
}
