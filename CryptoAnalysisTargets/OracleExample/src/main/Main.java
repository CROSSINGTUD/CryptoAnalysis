package main;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Main {
	public static void main(String...args) throws GeneralSecurityException{
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
	
	public static void keyStoreExample() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore instance = KeyStore.getInstance("Test");
		String pwdAsString = "Test";
		char[] password = pwdAsString.toCharArray();
		instance.load(null, password);
	}
}
