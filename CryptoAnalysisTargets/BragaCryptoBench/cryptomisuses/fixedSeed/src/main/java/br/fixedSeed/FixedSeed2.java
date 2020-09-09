package br.fixedSeed;

import java.security.SecureRandom;

public final class FixedSeed2 {

	public static void main(String[] args) {
		try {
			final int fixedSeed = 10;
			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed(10);
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed(fixedSeed);
		} catch (Exception e) {
		}
	}

}
