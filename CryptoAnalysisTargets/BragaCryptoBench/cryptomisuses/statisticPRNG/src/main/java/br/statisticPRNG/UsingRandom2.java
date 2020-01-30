package br.statisticPRNG;

import java.util.Random;
import java.security.SecureRandom;

public final class UsingRandom2 {

	public static void main(String[] args) {
		try {

			Random r3 = new Random();
			r3.setSeed((SecureRandom.getInstanceStrong()).nextInt());
			Random r4 = new Random();
			r4.setSeed((SecureRandom.getInstanceStrong()).nextInt());

		} catch (Exception e) {
		}
	}

}
