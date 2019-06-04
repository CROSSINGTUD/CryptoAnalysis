package example;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CipherUsesNonRandomKeyExample {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		String plainText = "Message";

		String key = "/u6=la%g57%Bnci(";
		byte[] keyBytes = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

		Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
		aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encrypted = aesCipher.doFinal(plainText.getBytes());
	}

}
