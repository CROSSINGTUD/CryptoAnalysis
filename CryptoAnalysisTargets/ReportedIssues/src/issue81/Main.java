package issue81;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.PBEKeySpec;

public class Main {
	public static void main(String...args) throws GeneralSecurityException {
		byte [] next = new byte[32];
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.nextBytes(next);
		PBEKeySpec pBEKeySpec = new PBEKeySpec("password".toCharArray(), next, 10299, 128);
	}
}
