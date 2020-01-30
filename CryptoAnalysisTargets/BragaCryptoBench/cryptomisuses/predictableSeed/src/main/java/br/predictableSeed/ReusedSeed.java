package br.predictableSeed;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class ReusedSeed {

	public static void main(String[] args) {
		try {
			SecureRandom sr4 = SecureRandom.getInstance("SHA1PRNG", "SUN");
			SecureRandom sr5 = SecureRandom.getInstance("SHA1PRNG", "SUN");
			byte[] seed = sr4.generateSeed(32);
			sr4.setSeed(seed);
			sr5.setSeed(seed);

			SecureRandom sr9 = SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");
			SecureRandom sr0 = SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");

			sr9.setSeed(seed);
			sr0.setSeed(seed);

		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
		}
	}
}
