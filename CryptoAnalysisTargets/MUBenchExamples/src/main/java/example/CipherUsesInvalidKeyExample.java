package example;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CipherUsesInvalidKeyExample {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String plainText = "Message";
		int keySize = 12;
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
		keygenerator.init(keySize);
		SecretKey key = keygenerator.generateKey();

		Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
		aesCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = aesCipher.doFinal(plainText.getBytes());
	}

}
