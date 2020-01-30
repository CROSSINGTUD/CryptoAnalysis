package br.fixedSeed;

import java.security.SecureRandom;

public final class FixedSeed3 {

	public static void main(String[] args) {
		try {
			byte[] fixedSeed = { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
					(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
					(byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

			SecureRandom r3 = SecureRandom.getInstanceStrong();
			r3.setSeed(10);
			SecureRandom r4 = SecureRandom.getInstanceStrong();
			r4.setSeed(fixedSeed);
		} catch (Exception e) {
		}
	}

}
