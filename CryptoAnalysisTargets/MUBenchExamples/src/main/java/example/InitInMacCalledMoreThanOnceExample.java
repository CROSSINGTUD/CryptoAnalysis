package example;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

public class InitInMacCalledMoreThanOnceExample {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
		KeyGenerator keyGen1 = KeyGenerator.getInstance("AES");
		SecureRandom secRandom1 = new SecureRandom();
		keyGen1.init(secRandom1);
		Key key1 = keyGen1.generateKey();

		KeyGenerator keyGen2 = KeyGenerator.getInstance("AES");
		SecureRandom secRandom2 = new SecureRandom();
		keyGen2.init(secRandom2);
		Key key2 = keyGen1.generateKey();

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(key1);
		mac.init(key2);
	}

}
