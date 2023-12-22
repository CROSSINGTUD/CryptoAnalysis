package crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CipherExample {
	
	public static void cipherExampleOne() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("RSA");
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		c.init(Cipher.WRAP_MODE, key);
		c.doFinal("".getBytes());
	}
	
	public static void cipherExampleTwo() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("EC");
		keygen.initialize(256);
		KeyPair pair = keygen.generateKeyPair();
		PublicKey key = pair.getPublic();
		c.init(Cipher.ENCRYPT_MODE, key);
		c.doFinal("".getBytes());
	}
}
