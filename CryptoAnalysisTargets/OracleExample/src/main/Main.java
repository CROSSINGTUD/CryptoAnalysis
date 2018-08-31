package main;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Crypto.PWHasher;

public class Main {
	public static void main(String... args) throws GeneralSecurityException {
		byte[] plainText = args[0].getBytes();
		String secretKey = "SECRET";
		byte[] keyBytes = secretKey.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] doFinal = cipher.doFinal();
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(secretKeySpec);
		mac.doFinal(plainText);
	}

	public static void keyStoreExample()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore instance = KeyStore.getInstance("Test");
		String pwdAsString = "Test";
		char[] password = pwdAsString.toCharArray();
		instance.load(null, password);
	}

	public static void cipherUsageExample() throws GeneralSecurityException {
		String trans = "AES";
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance(trans);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public void templateUsage(char[] pwd) throws GeneralSecurityException {
		PWHasher pwHasher = new PWHasher();
		String pwdHash = pwHasher.createPWHash(pwd);
		Boolean t = pwHasher.verifyPWHash(pwd, pwdHash);
	}
	
	public static void interproceduralTypestate() throws GeneralSecurityException {
		String trans = "AES/CBC/PKCS5Padding";
		Cipher cCipher = Cipher.getInstance(trans);
		use(cCipher);
	}

	private static void use(Cipher cCipher) throws IllegalBlockSizeException, BadPaddingException {
		byte[] encText = cCipher.doFinal("".getBytes());
	}
	
	public void incorrectKeyForWrongCipher() throws GeneralSecurityException{
		Object object = new Object();
		use(object);
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("Blowfish");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public void use(Object object) {
		
	}
	public void useCorrectDoFinal() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal("".getBytes());
	}

	public void useWrongDoFinal() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();;
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encText = cCipher.doFinal();
	}

	public void useNoDoFinal() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();;
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
	}

	public void useDoFinalInLoop() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();;
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		for (int i=0; i<42; i++){
			cCipher.doFinal("".getBytes());
		}
	}
}
