
package Crypto;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/** @author CogniCrypt */
public class KeyDeriv {

	public SecretKey getKey(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, 128);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");
		spec.clearPassword();
		return ret;
	}
}
