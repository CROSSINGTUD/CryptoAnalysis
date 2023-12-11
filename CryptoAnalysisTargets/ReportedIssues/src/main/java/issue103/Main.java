package issue103;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class Main {

	public static void main(String[] args) throws GeneralSecurityException {
		byte[] seed = { 1, 2, 3 };

		new SecureRandom(seed); //Static Seed

		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed); //Static Seed

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256, random); // "Second parameter was not properly randomized"
		keyGen.generateKey();
		new IvParameterSpec(seed); // "First parameter was not properly randomized"
	}

}
