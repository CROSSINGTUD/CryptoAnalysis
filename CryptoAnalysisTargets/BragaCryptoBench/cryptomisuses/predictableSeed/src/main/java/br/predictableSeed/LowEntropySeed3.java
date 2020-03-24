package br.predictableSeed;

import java.security.SecureRandom;

public final class LowEntropySeed3 {

	public static void main(String[] args) {
		try {

			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed(System.currentTimeMillis());
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed(System.nanoTime());

		} catch (Exception e) {
		}
	}

}
