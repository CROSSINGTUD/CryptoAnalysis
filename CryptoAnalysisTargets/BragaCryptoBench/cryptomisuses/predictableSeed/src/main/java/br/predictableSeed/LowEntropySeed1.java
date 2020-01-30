package br.predictableSeed;

import java.util.Random;
import java.security.SecureRandom;

public final class LowEntropySeed1 {

	public static void main(String[] args) {
		try {

			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed((new Random()).nextInt());
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed((new Random()).nextInt());

		} catch (Exception e) {
		}
	}

}
