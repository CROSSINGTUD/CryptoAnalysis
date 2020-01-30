package cai.undefinedCSP;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class UndefinedProvider3 {

	public static void main(String[] args) {
		try {
			SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG");
			SecureRandom r2 = SecureRandom.getInstanceStrong();
			r1.setSeed(r2.nextLong());

		} catch (NoSuchAlgorithmException e) {
		}
	}

}
