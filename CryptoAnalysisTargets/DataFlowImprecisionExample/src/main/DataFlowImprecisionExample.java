package main;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class DataFlowImprecisionExample {
	static String field;

	public static void main(String... args){
	}

	public static void cipherUsageExampleUsingStringConstantInVariable() throws GeneralSecurityException {
		String trans = "AES";
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance(trans);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public static void cipherUsageExampleUsingStringConstantDirectly() throws GeneralSecurityException {
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public static void cipherUsageExampleUsingStringObjectInVariable() throws GeneralSecurityException {
		String trans = new String("AES");
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance(trans);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public static void cipherUsageExampleUsingStringObjectDirectly() throws GeneralSecurityException {
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance(new String("AES"));
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public static void cipherUsageExampleUsingFieldWithStringConstant() throws GeneralSecurityException {
		field = "AES";
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance(field);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public static void cipherUsageExampleUsingFieldWithStringObject() throws GeneralSecurityException {
		field = new String("AES");
		SecretKey key =getKey();
		Cipher cCipher = Cipher.getInstance(field);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	/**
	 * Separate method to get key to ensure it is equal across all tests
	 */
	public static SecretKey getKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		return keygen.generateKey();
	}

}
