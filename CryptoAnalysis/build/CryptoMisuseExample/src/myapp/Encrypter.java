package myapp;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Encrypter {
	Cipher cipher;

	public Encrypter() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
	}

	public byte[] encrypt(String plainText) throws GeneralSecurityException {
		byte[] encText = this.cipher.doFinal(plainText.getBytes());
		return encText;
	}
}