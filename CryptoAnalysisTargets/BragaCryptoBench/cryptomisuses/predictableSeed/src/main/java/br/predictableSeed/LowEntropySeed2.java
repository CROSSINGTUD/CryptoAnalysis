package br.predictableSeed;

import java.security.SecureRandom;

public final class LowEntropySeed2 {

	public static void main(String[] args) {
		try {
			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed((int) (Math.random() * 10000));
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed((int) (100000 * Math.random()));

		} catch (Exception e) {
		}
	}

}
