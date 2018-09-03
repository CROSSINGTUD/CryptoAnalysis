package issuecognicrypt210;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CogniCryptSecretKeySpec {
	public static void main(String [] args)
			throws GeneralSecurityException, NoSuchAlgorithmException, InvalidKeySpecException {

		char[] password = new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
		byte [] next = new byte[32];
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.nextBytes(next);

		PBEKeySpec pBEKeySpec = new PBEKeySpec(password, next, 10299, 128);

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		SecretKey secretKey = secretKeyFactory.generateSecret(pBEKeySpec);

		byte[] keyMaterial = secretKey.getEncoded();

		SecretKeySpec secretKeySpec = new SecretKeySpec(keyMaterial, "AES");
		pBEKeySpec.clearPassword();
		
	}
}