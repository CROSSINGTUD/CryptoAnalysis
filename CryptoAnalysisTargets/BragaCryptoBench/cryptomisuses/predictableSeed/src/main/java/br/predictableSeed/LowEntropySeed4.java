package br.predictableSeed;

import java.security.SecureRandom;

public final class LowEntropySeed4 {

	public static void main(String[] args) {
		try {

			SecureRandom r3 = new SecureRandom();
			r3.setSeed(System.nanoTime());
			SecureRandom r4 = new SecureRandom();
			r4.setSeed(System.nanoTime());

		} catch (Exception e) {
		}
	}

}
